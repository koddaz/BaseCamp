package com.basecampers.basecamp.tabs.profile.models

import androidx.room.TypeConverter
import com.basecampers.basecamp.company.models.UserStatus

class UserStatusConverter {
	
	@TypeConverter
	fun fromUserStatus(value: UserStatus): String {
		return value.name // Convert enum to String
	}
	
	@TypeConverter
	fun toUserStatus(value: String): UserStatus {
		return UserStatus.valueOf(value) // Convert String back to enum
	}
}
