package com.example.model

import java.util.UUID

data class StudentUser(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val studentId: String = "",
    val role: String = "student", // "student" or "admin"
    val isModerated: Boolean = false,
    val avatarUrl: String = ""
)

data class ClassSchedule(
    val id: String = UUID.randomUUID().toString(),
    val courseCode: String = "",
    val courseName: String = "",
    val instructor: String = "",
    val dayOfWeek: String = "Monday", // Monday, Tuesday, etc.
    val startTime: String = "09:00", // HH:mm format
    val endTime: String = "10:30",
    val location: String = "",
    val notes: String = ""
)

data class GradeCourse(
    val id: String = UUID.randomUUID().toString(),
    val courseCode: String = "",
    val courseName: String = "",
    val credits: Int = 3,
    val gradeValue: Double = 4.0, // e.g. 4.0 for A, 3.0 for B
    val gradeLetter: String = "A", // A, B+, B, C+, C, D, F
    val assignmentsScore: Double = 90.0, // 0-100
    val examsScore: Double = 90.0, // 0-100
    val weightAssignments: Double = 0.4,
    val weightExams: Double = 0.6
)

data class StudyRequest(
    val id: String = UUID.randomUUID().toString(),
    val studentName: String = "",
    val studentEmail: String = "",
    val courseCode: String = "",
    val topic: String = "",
    val location: String = "",
    val time: String = "",
    val sizeLimit: Int = 4,
    val currentMembers: List<String> = emptyList() // List of student emails
)

data class ClubChannel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val category: String = "club", // "club" or "event" or "general"
    val icon: String = "groups"
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val channelId: String = "",
    val senderName: String = "",
    val senderEmail: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Announcement(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val date: String = "",
    val author: String = "Campus Admin",
    val isHighPriority: Boolean = false
)

data class CampusLandmark(
    val id: String = "",
    val name: String = "",
    val type: String = "", // "lecture_hall", "amenity", "library", "recreation"
    val description: String = "",
    val x: Float = 0f, // coordinates relative to a 1000x1000 campus canvas
    val y: Float = 0f,
    val rooms: List<String> = emptyList()
)
