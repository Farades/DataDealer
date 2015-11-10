package ru.entel.smiu.datadealer.protocols.registers;

/**
 * Enum RegType - перечисление типов данных в которых хранятся значения регистров.
 * FLOAT32      - Формат представления числа с плавающей запятой стандарта IEE 754
 * INT16        - Обычное целое число
 * INT16DIV10   - целое число, разделенное на 10
 * INT16DIV100  - целое число, разделенное на 100
 * BIT          - 1 бит (boolean)
 */
public enum RegType {
    FLOAT32,
    INT16,
    INT16DIV10,
    INT16DIV100,
    BIT
}
