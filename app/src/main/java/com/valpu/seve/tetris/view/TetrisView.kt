package com.valpu.seve.tetris.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.Toast
import com.github.johnpersano.supertoasts.library.Style
import com.github.johnpersano.supertoasts.library.SuperActivityToast
import com.github.johnpersano.supertoasts.library.utils.PaletteUtils
import com.valpu.seve.tetris.activities.GameActivity
import com.valpu.seve.tetris.constants.CellConstants
import com.valpu.seve.tetris.constants.FieldConstants
import com.valpu.seve.tetris.models.AppModel
import com.valpu.seve.tetris.models.Block

class TetrisView : View {

    private val paint = Paint()
    private var lastMove: Long = 0
    private var model: AppModel? = null
    private var activity: GameActivity? = null
    private val viewHandler = ViewHandler(this)
    private var cellSize: Dimension = Dimension(0, 0)
    private var frameOffSet: Dimension = Dimension(0, 0)

    constructor(ctx: Context, attrs: AttributeSet): super(ctx, attrs)
    constructor(ctx: Context, attrs: AttributeSet, defStyle: Int): super(ctx, attrs, defStyle)

    companion object {
        private const val DELAY = 500
        private const val BLOCK_OFFSET = 2
        private const val FRAME_OFFSET_BASE = 10
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawFrame(canvas)

        if (model != null) {
            for (i in 0 until FieldConstants.ROW_COUNT.value) {
                for (j in 0 until FieldConstants.COLUMN_COUNT.value) {
                    drawCell(canvas, i, j)
                }
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val cellWidth = (w - 2 * FRAME_OFFSET_BASE) / FieldConstants.COLUMN_COUNT.value
        val cellHeight = (h - 2 * FRAME_OFFSET_BASE) / FieldConstants.ROW_COUNT.value

        val n = Math.min(cellWidth, cellHeight)
        this.cellSize = Dimension(n, n)

        val offSetX = (w - FieldConstants.COLUMN_COUNT.value * n) / 2
        val offSetY = (h - FieldConstants.ROW_COUNT.value * n) / 2
        this.frameOffSet = Dimension(offSetX, offSetY)
    }

    private class ViewHandler(private val owner: TetrisView) : Handler() {

        override fun handleMessage(msg: Message) {

            if (msg.what == 0) {
                if (owner.model != null) {
                    if (owner.model!!.isGameOver()) {
                        owner.model?.endGame()
                        val superActivityToast = SuperActivityToast(owner.context)
                        superActivityToast.text = "                                   GAME OVER"
                        superActivityToast.duration = Style.DURATION_VERY_SHORT
                        superActivityToast.color = PaletteUtils.getSolidColor(PaletteUtils.MATERIAL_DEEP_ORANGE)
                        superActivityToast.textColor = Color.BLACK
                        //superActivityToast.setTouchToDismiss(true)
                        //superActivityToast.isIndeterminate = true
                        //superActivityToast.progressIndeterminate = true
                        superActivityToast.show()
                    }
                    if (owner.model!!.isGameActive()) {
                        owner.setGameCommandWithDelay(AppModel.Motions.DOWN)
                    }
                }
            }
        }

        fun sleep(delay: Long) {
            this.removeMessages(0)
            sendMessageDelayed(obtainMessage(0), delay)
        }
    }

    private data class Dimension(val width: Int, val height: Int)

    private fun drawFrame(canvas: Canvas) {
        paint.color = Color.LTGRAY

        canvas.drawRect(frameOffSet.width.toFloat(), frameOffSet.height.toFloat(),
                width - frameOffSet.width.toFloat(), height - frameOffSet.height.toFloat(),
                paint)
    }

    private fun drawCell(canvas: Canvas, row: Int, column: Int) {
        val cellStatus = model?.getCellStatus(row, column)

        if (CellConstants.EMPTY.value != cellStatus) {
            val color = if (CellConstants.EPHEMERAL.value == cellStatus) {
                model?.currentBlock?.getColor()
            } else {
                Block.getColor(cellStatus as Byte)
            }
            drawCell(canvas, column, row, color as Int)
        }
    }

    private fun drawCell(canvas: Canvas, x: Int, y: Int, rgbColor: Int) {
        paint.color = rgbColor

        val top: Float = (frameOffSet.height + y * cellSize.height + BLOCK_OFFSET).toFloat()
        val left: Float = (frameOffSet.width + x * cellSize.width + BLOCK_OFFSET).toFloat()
        val bottom: Float = (frameOffSet.height + (y + 1) * cellSize.height - BLOCK_OFFSET).toFloat()
        val right: Float = (frameOffSet.width + (x + 1) * cellSize.width - BLOCK_OFFSET).toFloat()

        val rectangle = RectF(left, top, right, bottom)

        canvas.drawRoundRect(rectangle, 4F, 4F, paint)
    }

    fun setModel(model: AppModel) { this.model = model }

    fun setActivity(gameActivity: GameActivity) { this.activity = gameActivity }

    fun setGameCommand(move: AppModel.Motions) {
        if (null != model && (model?.currentState == AppModel.Statuses.ACTIVE.name)) {

            if (AppModel.Motions.DOWN == move) {
                model?.generateField(move.name)
                invalidate()
                return
            }
            setGameCommandWithDelay(move)
        }
    }

    fun setGameCommandWithDelay(move: AppModel.Motions) {
        val now = System.currentTimeMillis()

        if (now - lastMove > DELAY) {
            model?.generateField(move.name)
            invalidate()
            lastMove = now
        }

        updateScores()
        viewHandler.sleep(DELAY.toLong())
    }

    private fun updateScores() {
        activity?.tvCurrentScore?.text = "${model?.score}"
        activity?.tvHighScore?.text = "${activity?.appPreferences?.getHighScore()}"
    }
}