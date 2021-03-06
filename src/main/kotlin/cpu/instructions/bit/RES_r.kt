package cpu.instructions.bit

import cpu.RegisterID
import cpu.Registers
import cpu.instructions.Instruction
import memory.Mmu
import utils.clearBit

class RES_r(registers: Registers, mmu: Mmu, private val register: Int, private val index: Int) : Instruction(registers, mmu) {

    override fun execute(): Int {

        when(register) {
            RegisterID.A.ordinal -> registers.A = clearBit(registers.A, index)
            RegisterID.B.ordinal -> registers.B = clearBit(registers.B, index)
            RegisterID.C.ordinal -> registers.C = clearBit(registers.C, index)
            RegisterID.D.ordinal -> registers.D = clearBit(registers.D, index)
            RegisterID.E.ordinal -> registers.E = clearBit(registers.E, index)
            RegisterID.H.ordinal -> registers.H = clearBit(registers.H, index)
            RegisterID.L.ordinal -> registers.L = clearBit(registers.L, index)
            else -> throw Exception("Invalid register: $register")
        }

        return 8
    }
}