package com.example.graph_app.presentation.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.graph_app.domain.interactor.PointInteractor

class ChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs), ScaleGestureDetector.OnScaleGestureListener {

    private var points: List<PointInteractor> = emptyList()
    private var scaleFactor = 1f
    private val scaleDetector = ScaleGestureDetector(context, this)
    private val gestureDetector = GestureDetector(context, GestureListener())

    private var offsetX = 0f
    private var offsetY = 0f

    private val pointRadius = 6f

    private val paddingLeft = 100f + pointRadius
    private val paddingBottom = 100f + pointRadius
    private val paddingRight = 50f + pointRadius
    private val paddingTop = 50f + pointRadius

    private var viewWidth: Float = 0f
    private var viewHeight: Float = 0f

    private var maxOffsetX = 0f
    private var minOffsetX = 0f
    private var maxOffsetY = 0f
    private var minOffsetY = 0f

    private val boundary = 100f


    var bitmap: Bitmap? = null



    fun setPoints(points: List<PointInteractor>) {
        this.points = points.sortedBy { it.x }
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        viewWidth = w - paddingLeft - paddingRight
        viewHeight = h - paddingTop - paddingBottom

        updateOffsetsBounds()
    }

    private fun updateOffsetsBounds() {
        val scaledWidth = viewWidth * scaleFactor
        val scaledHeight = viewHeight * scaleFactor

        val screenWidth = width - paddingLeft - paddingRight
        val screenHeight = height - paddingTop - paddingBottom

        if (scaledWidth + 2 * boundary > screenWidth) {
            minOffsetX = -((scaledWidth - screenWidth) + boundary)
            maxOffsetX = boundary
            offsetX = offsetX.coerceIn(minOffsetX, maxOffsetX)
        } else {
            minOffsetX = 0f
            maxOffsetX = 0f
            offsetX = (screenWidth - scaledWidth) / 2
        }

        if (scaledHeight + 2 * boundary > screenHeight) {
            minOffsetY = -((scaledHeight - screenHeight) + boundary)
            maxOffsetY = boundary
            offsetY = offsetY.coerceIn(minOffsetY, maxOffsetY)
        } else {
            minOffsetY = 0f
            maxOffsetY = 0f
            offsetY = (screenHeight - scaledHeight) / 2
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        updateOffsetsBounds()

        canvas.save()

        canvas.translate(paddingLeft + offsetX, paddingTop + offsetY)
        canvas.scale(scaleFactor, scaleFactor)

        val minX = points.minOfOrNull { it.x } ?: 0.0
        val maxX = points.maxOfOrNull { it.x } ?: (minX + 1.0)
        val minY = points.minOfOrNull { it.y } ?: 0.0
        val maxY = points.maxOfOrNull { it.y } ?: (minY + 1.0)

        val deltaX = if (maxX - minX != 0.0) maxX - minX else 1.0
        val deltaY = if (maxY - minY != 0.0) maxY - minY else 1.0

        val scaleX = (viewWidth / deltaX).toFloat()
        val scaleY = (viewHeight / deltaY).toFloat()

        drawAxes(canvas, viewWidth, viewHeight)
        drawXTicks(canvas, minX, maxX, scaleX, viewHeight)
        drawYTicks(canvas, minY, maxY, scaleY, viewHeight)
        drawXLabels(canvas, minX, maxX, scaleX, viewHeight)
        drawYLabels(canvas, minY, maxY, scaleY, viewHeight)

        val maxStrokeWidth = maxOf(4f, pointRadius)

        val clipRect = RectF(
            -maxStrokeWidth / 2,
            -maxStrokeWidth / 2,
            viewWidth + maxStrokeWidth / 2,
            viewHeight + maxStrokeWidth / 2
        )
        canvas.save()
        canvas.clipRect(clipRect)

        drawGraph(canvas, minX, maxX, minY, maxY, scaleX, scaleY, viewHeight)

        drawDataPoints(canvas, minX, minY, scaleX, scaleY, viewHeight)

        canvas.restore()

        canvas.restore()
    }

    private fun drawAxes(canvas: Canvas, viewWidth: Float, viewHeight: Float) {
        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 2f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        canvas.drawLine(
            0f,
            viewHeight,
            viewWidth,
            viewHeight,
            paint
        )

        canvas.drawLine(
            0f,
            0f,
            0f,
            viewHeight,
            paint
        )
    }

    private fun drawXTicks(
        canvas: Canvas,
        minX: Double,
        maxX: Double,
        scaleX: Float,
        viewHeight: Float,
        tickCount: Int = 5
    ) {
        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val tickLength = 10f
        val rangeX = maxX - minX
        for (i in 0..tickCount) {
            val value = minX + i * rangeX / tickCount
            val x = ((value - minX) * scaleX)

            canvas.drawLine(
                x.toFloat(),
                viewHeight,
                x.toFloat(),
                viewHeight - tickLength,
                paint
            )
        }
    }

    private fun drawYTicks(
        canvas: Canvas,
        minY: Double,
        maxY: Double,
        scaleY: Float,
        viewHeight: Float,
        tickCount: Int = 5
    ) {
        val paint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val tickLength = 10f
        val rangeY = maxY - minY
        for (i in 0..tickCount) {
            val value = minY + i * rangeY / tickCount
            val y = (viewHeight - (value - minY) * scaleY)

            canvas.drawLine(
                0f,
                y.toFloat(),
                tickLength,
                y.toFloat(),
                paint
            )
        }
    }

    private fun drawXLabels(
        canvas: Canvas,
        minX: Double,
        maxX: Double,
        scaleX: Float,
        viewHeight: Float,
        tickCount: Int = 5
    ) {
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val offsetY = viewHeight + 30f

        val rangeX = maxX - minX
        for (i in 0..tickCount) {
            val value = minX + i * rangeX / tickCount
            val x = ((value - minX) * scaleX)

            canvas.drawText(
                String.format("%.2f", value),
                x.toFloat(),
                offsetY,
                paint
            )
        }
    }

    private fun drawYLabels(
        canvas: Canvas,
        minY: Double,
        maxY: Double,
        scaleY: Float,
        viewHeight: Float,
        tickCount: Int = 5
    ) {
        val paint = Paint().apply {
            color = Color.BLACK
            textSize = 30f
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }

        val offsetX = -10f

        val rangeY = maxY - minY
        for (i in 0..tickCount) {
            val value = minY + i * rangeY / tickCount
            val y = (viewHeight - (value - minY) * scaleY) + 10f

            canvas.drawText(
                String.format("%.2f", value),
                offsetX,
                y.toFloat(),
                paint
            )
        }
    }

    private fun drawGraph(
        canvas: Canvas,
        minX: Double,
        maxX: Double,
        minY: Double,
        maxY: Double,
        scaleX: Float,
        scaleY: Float,
        viewHeight: Float
    ) {
        if (points.isEmpty()) return

        val paint = Paint().apply {
            color = Color.BLUE
            strokeWidth = 4f
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val screenPoints = points.map { point ->
            PointF(
                ((point.x - minX) * scaleX).toFloat(),
                (viewHeight - (point.y - minY) * scaleY).toFloat()
            )
        }

        val path = Path()

        path.moveTo(screenPoints[0].x, screenPoints[0].y)

        for (i in 0 until screenPoints.size - 1) {
            val currentPoint = screenPoints[i]
            val nextPoint = screenPoints[i + 1]

            val controlPoint1 = PointF(
                (currentPoint.x + nextPoint.x) / 2,
                currentPoint.y
            )

            val controlPoint2 = PointF(
                (currentPoint.x + nextPoint.x) / 2,
                nextPoint.y
            )

            path.cubicTo(
                controlPoint1.x, controlPoint1.y,
                controlPoint2.x, controlPoint2.y,
                nextPoint.x, nextPoint.y
            )
        }

        canvas.drawPath(path, paint)
    }

    private fun drawDataPoints(
        canvas: Canvas,
        minX: Double,
        minY: Double,
        scaleX: Float,
        scaleY: Float,
        viewHeight: Float
    ) {
        val paint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        for (point in points) {
            val x = ((point.x - minX) * scaleX)
            val y = (viewHeight - (point.y - minY) * scaleY)
            canvas.drawCircle(x.toFloat(), y.toFloat(), pointRadius, paint)
        }
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor *= detector.scaleFactor
        scaleFactor = scaleFactor.coerceIn(0.5f, 5f)

        updateOffsetsBounds()

        invalidate()
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        updateOffsetsBounds()
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float
        ): Boolean {
            offsetX -= distanceX
            offsetY -= distanceY

            offsetX = offsetX.coerceIn(minOffsetX, maxOffsetX)
            offsetY = offsetY.coerceIn(minOffsetY, maxOffsetY)

            invalidate()
            return true
        }
    }

    fun getGraphBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val bgDrawable = background
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }

        draw(canvas)

        return bitmap
    }

}