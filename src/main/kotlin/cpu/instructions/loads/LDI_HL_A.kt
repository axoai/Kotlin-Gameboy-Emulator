package cpu.instructions.loads

import memory.Mmu
import cpu.Registers
import cpu.instructions.Instruction

class LDI_HL_A(registers: Registers, mmu: Mmu) : Instruction(registers, mmu) {

    override fun execute(): Int {

        val HL = registers.getHL()
        registers.setHL(HL + 1)

        mmu.writeByte(HL, registers.A)

        return 8
    }
}