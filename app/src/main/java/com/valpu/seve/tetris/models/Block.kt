package com.valpu.seve.tetris.models

import android.graphics.Color
import android.graphics.Point
import android.support.annotation.NonNull
import com.valpu.seve.tetris.constants.FieldConstants

import java.util.Random

class Block(private val shapeIndex: Int, private val color: BlockColor) {

    private var frameNumber: Int
    private var position: Point?

    init {
        this.frameNumber = 0
        this.position = Point(FieldConstants.COLUMN_COUNT.value / 2, 0)
    }

    enum class BlockColor(val rgbValue: Int, val byteValue: Byte) {
        PINK(Color.rgb(255, 105, 180), 2.toByte()),
        GREEN(Color.rgb(0, 128, 0), 3.toByte()),
        ORANGE(Color.rgb(255, 140, 0), 4.toByte()),
        YELLOW(Color.rgb(255, 255, 0), 5.toByte()),
        CYAN(Color.rgb(0, 255, 255), 6.toByte())

    }

    companion object {

        fun createBlock(): Block {
            val random = Random()
            val shapeIndex = random.nextInt(Shape.values().size)
            val blockColor = BlockColor.values()[random.nextInt(BlockColor
                    .values().size)]

            val block = Block(shapeIndex, blockColor)
            // Set to the middle
            block.position!!.x = block.position!!.x - Shape.values()[shapeIndex].startPosition

            return block

        }

        fun getColor(value: Byte): Int {
            for (colour in BlockColor.values()) {
                if (value == colour.byteValue) {
                    return colour.rgbValue
                }
            }
            return -1
        }
    }


    fun setState(frame: Int, position: Point) {
        this.frameNumber = frame
        this.position = position
    }

    @NonNull
    fun getShape(frameNumber: Int): Array<ByteArray> {
        return Shape.values()[shapeIndex].getFrame(frameNumber).get2dByteArray()
    }

    fun getColor(): Int {
        return color.rgbValue
    }

    fun getFrameNumber(): Int {
        return frameNumber
    }

    fun getPosition(): Point? {
        return this.position
    }

    val frameCount: Int
        get() = Shape.values()[shapeIndex].frameCount

    val staticValue: Byte
        get() = color.byteValue
}