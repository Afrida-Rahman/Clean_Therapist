package org.mmh.clean_therapist.android.feature_exercise.domain.posedetector.ml_kit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.View
import com.google.common.base.Preconditions
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Person
import org.mmh.clean_therapist.android.feature_exercise.domain.model.Phase


class GraphicOverlay(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val lock = Any()
    private val graphics: MutableList<Graphic> = ArrayList()

    // Matrix for transforming from image coordinates to overlay view coordinates.
    private val transformationMatrix = Matrix()
    private var imageWidth = 0
    private var imageHeight = 0

    // The factor of overlay View size to image size. Anything in the image coordinates need to be
    // scaled by this amount to fit with the area of overlay View.
    private var scaleFactor = 1.0f

    // The number of horizontal pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private var postScaleWidthOffset = 0f

    // The number of vertical pixels needed to be cropped on each side to fit the image with the
    // area of overlay View after scaling.
    private var postScaleHeightOffset = 0f
    var isImageFlipped = false
    lateinit var phases: List<Phase>
    var person = Person(listOf(), 0f)

    private var needUpdateTransformation = true

    abstract class Graphic(private val overlay: GraphicOverlay) {

        abstract fun drawBodyKeyPoints(canvas: Canvas, phases: List<Phase>) : Person?

        private fun scale(imagePixel: Float): Float {
            return imagePixel * overlay.scaleFactor
        }

        fun translateX(x: Float): Float {
            return if (overlay.isImageFlipped) {
                overlay.width - (scale(x) - overlay.postScaleWidthOffset)
            } else {
                scale(x) - overlay.postScaleWidthOffset
            }
        }

        fun translateY(y: Float): Float {
            return scale(y) - overlay.postScaleHeightOffset
        }

        fun getTransformationMatrix(): Matrix {
            return overlay.transformationMatrix
        }
    }

    fun clear() {
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    fun add(graphic: Graphic) {
        synchronized(lock) { graphics.add(graphic) }
    }

    fun setImageSourceInfo(imageWidth: Int, imageHeight: Int, isFlipped: Boolean) {
        Preconditions.checkState(imageWidth > 0, "image width must be positive")
        Preconditions.checkState(imageHeight > 0, "image height must be positive")
        synchronized(lock) {
            this.imageWidth = imageWidth
            this.imageHeight = imageHeight
            isImageFlipped = isFlipped
            needUpdateTransformation = true
        }
        postInvalidate()
    }

    private fun updateTransformationIfNeeded() {
        if (!needUpdateTransformation || imageWidth <= 0 || imageHeight <= 0) {
            return
        }
        val viewAspectRatio = width.toFloat() / height
        val imageAspectRatio = imageWidth.toFloat() / imageHeight
        postScaleWidthOffset = 0f
        postScaleHeightOffset = 0f
        if (viewAspectRatio > imageAspectRatio) {
            // The image needs to be vertically cropped to be displayed in this view.
            scaleFactor = width.toFloat() / imageWidth
            postScaleHeightOffset = (width.toFloat() / imageAspectRatio - height) / 2
        } else {
            // The image needs to be horizontally cropped to be displayed in this view.
            scaleFactor = height.toFloat() / imageHeight
            postScaleWidthOffset = (height.toFloat() * imageAspectRatio - width) / 2
        }
        transformationMatrix.reset()
        transformationMatrix.setScale(scaleFactor, scaleFactor)
        transformationMatrix.postTranslate(-postScaleWidthOffset, -postScaleHeightOffset)
        if (isImageFlipped) {
            transformationMatrix.postScale(-1f, 1f, width / 2f, height / 2f)
        }
        needUpdateTransformation = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        synchronized(lock) {
            updateTransformationIfNeeded()
            for (graphic in graphics) {
                if(graphic.drawBodyKeyPoints(canvas, phases) != null)
                    person = graphic.drawBodyKeyPoints(canvas, phases)!!
            }
        }
    }

    init {
        addOnLayoutChangeListener { _: View?, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int, _: Int ->
            needUpdateTransformation = true
        }
    }
}