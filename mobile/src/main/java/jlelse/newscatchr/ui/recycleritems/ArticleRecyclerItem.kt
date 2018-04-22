/*
 * NewsCatchr
 * Copyright © 2017 Jan-Lukas Else
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jlelse.newscatchr.ui.recycleritems

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.flexbox.FlexboxLayout
import jlelse.newscatchr.database
import jlelse.newscatchr.extensions.*
import jlelse.newscatchr.ui.fragments.ArticleView
import jlelse.newscatchr.ui.layout.ArticleRecyclerItemUI
import jlelse.readit.R
import jlelse.sourcebase.Article
import jlelse.viewmanager.ViewManagerView
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.find

class ArticleRecyclerItem(val article: Article? = null, val fragment: ViewManagerView? = null) : NCAbstractItem<ArticleRecyclerItem, ArticleRecyclerItem.ViewHolder>() {

	override fun getType(): Int {
		return R.id.article_item_id
	}

	override fun createView(ctx: Context, parent: ViewGroup?): View {
		return ArticleRecyclerItemUI().createView(AnkoContext.create(ctx, this))
	}

	override fun bindView(viewHolder: ViewHolder, payloads: MutableList<Any?>) {
		super.bindView(viewHolder, payloads)
		val context = viewHolder.itemView.context
		if (!article?.title.isNullOrBlank()) {
			viewHolder.title.isVisible = true
			viewHolder.title.text = article?.title
			viewHolder.title.setTypeface(null, if (database.isReadUrl(article?.link)) Typeface.BOLD_ITALIC else Typeface.BOLD)
		} else viewHolder.title.isGone = true
		if ((article?.time?.toInt() ?: 0) != 0) {
			viewHolder.details.isVisible = true
			val detailText = DateUtils.getRelativeTimeSpanString(article!!.time ?: 0)
			viewHolder.details.text = detailText
		} else viewHolder.details.isGone = true
		if (!article?.content.isNullOrBlank()) {
			viewHolder.content.isVisible = true
			viewHolder.content.text = article?.excerpt()
		} else viewHolder.content.isGone = true
		if (article?.tags.notNullAndEmpty()) {
			viewHolder.tagsBox.isVisible = true
			viewHolder.tagsBox.removeAllViews()
			viewHolder.tagsBox.addTags(fragment!!, article?.tags?.take(3))
		} else {
			//viewHolder.tagsBox.hideView()
		}
		if (!article?.image.isNullOrBlank()) {
			viewHolder.visual.isVisible = true
			viewHolder.visual.loadImage(article?.image)
		} else {
			viewHolder.visual.clearGlide()
			viewHolder.visual.isGone = true
		}
		viewHolder.itemView.setOnClickListener {
			if (article != null) fragment?.openView(ArticleView(article = article).withTitle(article.feedTitle))
		}
		viewHolder.bookmark.setImageDrawable((if (article.isBookmark()) R.drawable.ic_bookmark_universal else R.drawable.ic_bookmark_border_universal).resDrw(context, R.color.colorPrimaryText.resClr(context)))
		viewHolder.bookmark.setOnClickListener {
			if (article != null) {
				if (article.isBookmark()) {
					database.deleteBookmark(article.link)
					viewHolder.bookmark.setImageDrawable(R.drawable.ic_bookmark_border_universal.resDrw(context, R.color.colorPrimaryText.resClr(context)))
				} else {
					database.addBookmark(article)
					viewHolder.bookmark.setImageDrawable(R.drawable.ic_bookmark_universal.resDrw(context, R.color.colorPrimaryText.resClr(context)))
				}
			}
		}
		viewHolder.share.setImageDrawable(R.drawable.ic_share_universal.resDrw(context, R.color.colorPrimaryText.resClr(context)))
		viewHolder.share.setOnClickListener {
			if (fragment != null) article?.share(fragment.context)
		}
	}

	override fun getViewHolder(p0: View) = ViewHolder(p0)

	class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		var bookmark: ImageView = view.find(R.id.articlerecycleritem_bookmark)
		var share: ImageView = view.find(R.id.articlerecycleritem_share)
		var title: TextView = view.find(R.id.articlerecycleritem_title)
		var details: TextView = view.find(R.id.articlerecycleritem_details)
		var content: TextView = view.find(R.id.articlerecycleritem_content)
		var visual: ImageView = view.find(R.id.articlerecycleritem_visual)
		var tagsBox: FlexboxLayout = view.find(R.id.articlerecycleritem_tagsbox)
	}
}
