package cpu.instructions.alu

import memory.Mmu
import cpu.RegisterID
import cpu.Registers

class ADC_A_r(registers: Registers, mmu: Mmu, private val register: Int) : ADC(registers, mmu) {

    override fun execute(): Int {
        val value = when(register) {
            RegisterID.A.ordinal -> registers.A
            RegisterID.B.ordinal -> registers.B
            RegisterID.C.ordinal -> registers.C
            RegisterID.D.ordinal -> registers.D
            RegisterID.E.ordinal -> registers.E
            RegisterID.H.ordinal -> registers.H
            RegisterID.L.ordinal -> registers.L
            else -> throw Exception("Invalid register: " + register)
        }

        super.adc(value)

        return 4
    }
}