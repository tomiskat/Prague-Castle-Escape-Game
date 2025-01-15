package escape.game.views

import android.content.Context
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.TextView
import androidx.core.text.HtmlCompat

class ClickableTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setOnTouchListener { textview, event ->
            require(textview is TextView)
            val action = event.action
            if (action == MotionEvent.ACTION_UP) {
                val x = event.x.toInt() - textview.totalPaddingLeft + textview.scrollX
                val y = event.y.toInt() - textview.totalPaddingTop + textview.scrollY
                val line = textview.layout.getLineForVertical(y)
                val offset = textview.layout.getOffsetForHorizontal(line, x.toFloat())
                val spannable = SpannableString(text)
                val links: Array<ClickableSpan> = spannable.getSpans(offset, offset, ClickableSpan::class.java)
                if (links.isNotEmpty()) {
                    links.first().onClick(textview)
                    textview.performClick()
                }
            }
            true
        }
    }

    fun showHtml(html: String) {
        this.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}