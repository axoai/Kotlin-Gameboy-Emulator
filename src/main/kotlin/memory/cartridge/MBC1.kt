package memory.cartridge

import memory.Memory
import utils.getBit
import utils.toHexString

class MBC1(romBanks: Int, ramSize: Int) : Memory, MBC {

    override val ram: Array<IntArray>?
    override val rom: Array<IntArray>

    override var currentRomBank = 1
    override var currentRamBank = 0
    override var ramEnabled = false

    var mode = 0

    init {
        if (romBanks !in 0..128) {
            throw IllegalArgumentException("Illegal number of ROM banks: $romBanks")
        }
        rom = Array(romBanks) {IntArray(0x4000)}
        ram = when (ramSize) {
            0       -> null
            0x800   -> Array(1) {IntArray(0x800)}
            0x2000  -> Array(1) {IntArray(0x2000)}
            0x8000  -> Array(4) {IntArray(0x2000)}
            0x20000 -> Array(16) {IntArray(0x2000)}
            else -> throw IllegalArgumentException("Illegal RAM size: $ramSize")
        }
    }

    override fun reset() {
        super.reset()
        currentRamBank = 0
        currentRomBank = 1
        mode = 0
        ramEnabled = false
    }

    override fun loadRom(value: ByteArray) {
        for (i in 0 until value.size) {
            val bank: Int = i / 0x4000
            val index: Int = i - (bank * 0x4000)

            rom[bank][index] = (value[i].toInt()) and 0xFF
        }
    }

    override fun readRom(address: Int): Int {
        var romBank = if (mode == 1) currentRomBank and 0b00011111 else currentRomBank or (currentRamBank shl 5)
        return when(address) {
            in 0x0000 until 0x4000 -> {
                /*
                if (rom.size > 0b11111 && mode == 1) {
                    if (romBank in 32 until 64 || romBank in 96 until 128) {
                        this.rom[32][address]
                    }
                }*/
                this.rom[0][address]
            }
            in 0x4000 until 0x8000 -> {
                romBank %= rom.size
                this.rom[romBank][address - 0x4000]
            }
            else -> throw IllegalArgumentException("Address ${address.toHexString(2)} out of bounds")
        }
    }

    override fun writeRom(address: Int, value: Int) {
        when(address) {
            // RAM Enable
            in 0x0000 until 0x2000 -> ramEnabled = value.getBit(1) && value.getBit(3)

            // ROM Bank Number lower 5 bits
            in 0x2000 until 0x4000 -> {
                val newVal = value and 0b00011111
                var newRomBank = (currentRomBank and 0b11100000) or newVal

                if (newRomBank == 0) {
                    newRomBank++
                }

                currentRomBank = newRomBank % rom.size
            }

            // RAM Bank Number or Upper Bits of ROM Bank Number
            in 0x4000 until 0x6000 -> {
                val newVal = value and 0b00000011
                /*
                if (mode == 0) {
                    val newRomBank = currentRomBank or (newVal shl 5)
                    currentRomBank = newRomBank % rom.size

                } else {
                    // Check whether we actually have RAM and whether the selected bank actually exists
                    if (ram != null && newVal < ram.size) {
                        currentRamBank = newVal
                    }
                }*/
                currentRamBank = newVal
            }

            // ROM/RAM Mode Select
            in 0x6000 until 0x8000 -> {
                mode = value and 0b00000001
            }
        }
    }

    override fun readRam(address: Int): Int {
        if (address !in 0xA000 until 0xC000) {
            throw IllegalArgumentException("Address ${address.toHexString(2)} out of bounds")
        }

        if (ram == null || !ramEnabled) {
            return 0xFF
        }

        val newAddress = address - 0xA000
        return if (mode == 0) {
            this.ram[0][newAddress]
        } else {
            if (currentRamBank >= ram.size) {
                this.ram[0][newAddress]
            } else {
                this.ram[currentRamBank][newAddress]
            }
        }
    }

    override fun writeRam(address: Int, value: Int) {
        if (address !in 0xA000 until 0xC000) {
            throw IllegalArgumentException("Address ${address.toHexString(2)} out of bounds")
        }

        if (ram == null || !ramEnabled) {
            return
        }

        val newAddress = address - 0xA000
        val newVal = value and 0xFF
        if (mode == 0) {
            this.ram[0][newAddress] = newVal
        } else {
            if (currentRamBank >= ram.size) {
                this.ram[0][newAddress] = newVal
            } else {
                this.ram[currentRamBank][newAddress] = newVal
            }
        }
    }
}