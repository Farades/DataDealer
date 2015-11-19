package ru.entel.smiu.datadealer.hardware_engine.protocols.modbus;

/**
 * Enum ModbusFunction     - перечисление основных функций протокола Modbus
 * READ_COIL_REGS_1        - чтение значений из нескольких регистров флагов
 * READ_DISCRETE_INPUT_2   - чтение значений из нескольких дискретных входов
 * READ_HOLDING_REGS_3     - чтение значений из нескольких регистров хранения
 * READ_INPUT_REGS_4       - чтение значений из нескольких регистров ввода
 * WRITE_SINGLE_COIL_5     - запись значения одного флага
 * WRITE_SINGLE_REG_6      - запись значения в один регистр хранения
 * WRITE_MULTIPLE_COILS_15 - запись значений в несколько регистров флагов
 * WRITE_MULTIPLE_REGS_16  - запись значений в несколько регистров хранения
 */
public enum ModbusFunction {
    READ_COIL_REGS_1,
    READ_DISCRETE_INPUT_2,
    READ_HOLDING_REGS_3,
    READ_INPUT_REGS_4,
    WRITE_SINGLE_COIL_5,
    WRITE_SINGLE_REG_6,
    WRITE_MULTIPLE_COILS_15,
    WRITE_MULTIPLE_REGS_16
}
