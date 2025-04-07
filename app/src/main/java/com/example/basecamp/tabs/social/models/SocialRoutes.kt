package com.example.basecamp.tabs.social.models

// Routes for the social section
object socialRoutes {
    const val QNA = "qna"
    const val FORUM = "forum"
    const val MESSAGES = "messages"
}

// Enum representing the social tabs for easier tab handling
enum class SocialTab(val index: Int, val route: String) {
    QNA(0, socialRoutes.QNA),
    FORUM(1, socialRoutes.FORUM),
    MESSAGES(2, socialRoutes.MESSAGES);
    
    companion object {
        fun fromIndex(index: Int): SocialTab = values().firstOrNull { it.index == index } ?: FORUM
        fun fromRoute(route: String): SocialTab = values().firstOrNull { it.route == route } ?: FORUM
    }
}