package com.basecampers.basecamp.tabs.social.models

data class QnAItem(
	val id: String = "", // Document ID
	val question: String = "",
	val answer: String = "",
	val isPublished: Boolean = true
)