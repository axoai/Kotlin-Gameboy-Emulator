package cpu.instructions.alu

import memory.Mmu
import cpu.Registers

class CP_A_HL(registers: Registers, mmu: Mmu) : CP(registers, mmu) {

    override fun execute(): Int {

        val value = mmu.readByte(registers.getHL())

        super.cp(value)

        return 8
    }
}