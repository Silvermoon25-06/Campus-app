package com.example.db

import android.content.Context
import android.util.Log
import com.example.model.*
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class CampusRepository private constructor(context: Context) {

    private val sharedPrefs = context.getSharedPreferences("unicampus_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow<StudentUser?>(null)
    val currentUser: StateFlow<StudentUser?> = _currentUser.asStateFlow()

    private val _firebaseStatus = MutableStateFlow("Initializing...")
    val firebaseStatus: StateFlow<String> = _firebaseStatus.asStateFlow()

    private val _schedules = MutableStateFlow<List<ClassSchedule>>(emptyList())
    val schedules: StateFlow<List<ClassSchedule>> = _schedules.asStateFlow()

    private val _grades = MutableStateFlow<List<GradeCourse>>(emptyList())
    val grades: StateFlow<List<GradeCourse>> = _grades.asStateFlow()

    private val _studyRequests = MutableStateFlow<List<StudyRequest>>(emptyList())
    val studyRequests: StateFlow<List<StudyRequest>> = _studyRequests.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _channels = MutableStateFlow<List<ClubChannel>>(emptyList())
    val channels: StateFlow<List<ClubChannel>> = _channels.asStateFlow()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _users = MutableStateFlow<List<StudentUser>>(emptyList())
    val users: StateFlow<List<StudentUser>> = _users.asStateFlow()

    private var isFirebaseEnabled = false
    private var firebaseAuth: FirebaseAuth? = null
    private var firestore: FirebaseFirestore? = null

    init {
        try {
            // Check if Firebase is configured (if google-services.json is present, FirebaseApp is initialized)
            val app = FirebaseApp.initializeApp(context)
            if (app != null) {
                firebaseAuth = FirebaseAuth.getInstance()
                firestore = FirebaseFirestore.getInstance()
                isFirebaseEnabled = true
                _firebaseStatus.value = "Connected"
                Log.d("CampusRepository", "Firebase successfully initialized and connected!")
            } else {
                setupSandboxMode()
            }
        } catch (e: Exception) {
            Log.w("CampusRepository", "Firebase config missing or failed. Using high-fidelity local Sandbox Mode.")
            setupSandboxMode()
        }

        // Try load session from local SharedPreferences if present
        loadLocalSession()
    }

    private fun setupSandboxMode() {
        isFirebaseEnabled = false
        _firebaseStatus.value = "Sandbox Mode (No google-services.json)"

        // Pre-populate Club Channels
        _channels.value = listOf(
            ClubChannel("all", "Campus Announcements", "General notifications for all students", "general", "campaign"),
            ClubChannel("cs_club", "Computer Science Club", "Coding hackathons, tech talks, and project collabs", "club", "terminal"),
            ClubChannel("debate", "Debate Society", "Weekly debates on current affairs and public speaking training", "club", "forum"),
            ClubChannel("gaming", "Esports & Gaming", "Casual tournaments and community game nights", "club", "sports_esports"),
            ClubChannel("sports", "Intramural Sports", "Match calendars, registration, and team discussions", "club", "sports_soccer"),
            ClubChannel("fall_fest", "Fall Campus Festival", "Official channel for the upcoming campus-wide event", "event", "celebration")
        )

        // Pre-populate Announcements
        _announcements.value = mutableListOf(
            Announcement(
                title = "Welcome to the New Academic Year!",
                content = "Welcome back, students! The campus is fully prepared for an incredible semester of learning and growth. Be sure to check your class schedules, navigate via the Campus Map, and join a club channel to get connected.",
                date = "Jul 04, 2026",
                isHighPriority = true
            ),
            Announcement(
                title = "Midterm Examination Timetable Released",
                content = "The draft timetable for midterm examinations has been published on the student portal. Please check your schedule and report any clashes by next Friday.",
                date = "Jul 03, 2026",
                isHighPriority = false
            ),
            Announcement(
                title = "Annual Campus Career Fair next Thursday",
                content = "Meet recruiters from over 50 leading companies in engineering, finance, health, and tech. Bring printed resumes and dress in business casuals. Location: Student Union Multipurpose Hall.",
                date = "Jun 30, 2026",
                isHighPriority = false
            )
        )

        // Pre-populate Class Schedules
        _schedules.value = listOf(
            ClassSchedule(courseCode = "CS-301", courseName = "Design & Analysis of Algorithms", instructor = "Dr. Helen Vance", dayOfWeek = "Monday", startTime = "10:00", endTime = "11:30", location = "Science Hall, Room 402", notes = "Review master theorem for divide-and-conquer recurrences before class."),
            ClassSchedule(courseCode = "CS-301", courseName = "Design & Analysis of Algorithms", instructor = "Dr. Helen Vance", dayOfWeek = "Wednesday", startTime = "10:00", endTime = "11:30", location = "Science Hall, Room 402", notes = "Homework 1 due at start of class."),
            ClassSchedule(courseCode = "CS-342", courseName = "Database Management Systems", instructor = "Prof. Marcus Brody", dayOfWeek = "Monday", startTime = "13:00", endTime = "14:30", location = "Engineering Center, Hall B", notes = "Installing PostgreSQL locally before lectures is highly recommended."),
            ClassSchedule(courseCode = "CS-342", courseName = "Database Management Systems", instructor = "Prof. Marcus Brody", dayOfWeek = "Wednesday", startTime = "13:00", endTime = "14:30", location = "Engineering Center, Hall B", notes = ""),
            ClassSchedule(courseCode = "MATH-310", courseName = "Linear Algebra II", instructor = "Dr. Arthur Pendelton", dayOfWeek = "Tuesday", startTime = "09:00", endTime = "10:30", location = "Liberal Arts Building, Room 102", notes = "Bring calculators and lecture notes on eigenvalues."),
            ClassSchedule(courseCode = "MATH-310", courseName = "Linear Algebra II", instructor = "Dr. Arthur Pendelton", dayOfWeek = "Thursday", startTime = "09:00", endTime = "10:30", location = "Liberal Arts Building, Room 102", notes = "")
        )

        // Pre-populate Grade Tracker Courses
        _grades.value = listOf(
            GradeCourse(courseCode = "CS-201", courseName = "Data Structures", credits = 4, gradeLetter = "A", gradeValue = 4.0, assignmentsScore = 95.0, examsScore = 91.0),
            GradeCourse(courseCode = "MATH-201", courseName = "Discrete Mathematics", credits = 3, gradeLetter = "B+", gradeValue = 3.3, assignmentsScore = 88.0, examsScore = 84.0),
            GradeCourse(courseCode = "PHYS-150", courseName = "General Physics I", credits = 4, gradeLetter = "B", gradeValue = 3.0, assignmentsScore = 82.0, examsScore = 78.0),
            GradeCourse(courseCode = "CS-220", courseName = "Computer Architecture", credits = 3, gradeLetter = "A-", gradeValue = 3.7, assignmentsScore = 92.0, examsScore = 89.0)
        )

        // Pre-populate Study Requests
        _studyRequests.value = listOf(
            StudyRequest(UUID.randomUUID().toString(), "Ethan Walker", "ethan.walker@univ.edu", "CS-301", "DP & Greedy Algorithms prep", "Science Library 3rd Floor", "Today, 5:00 PM", 5, listOf("ethan.walker@univ.edu", "sarah.lee@univ.edu")),
            StudyRequest(UUID.randomUUID().toString(), "Clara Diaz", "clara.diaz@univ.edu", "CS-342", "SQL Query Optimization practice", "Engineering Center Atrium", "Tomorrow, 2:00 PM", 4, listOf("clara.diaz@univ.edu")),
            StudyRequest(UUID.randomUUID().toString(), "Jordan Wu", "jordan.wu@univ.edu", "MATH-310", "Eigenvalues & Diagonalization revision", "Student Union Cafe", "Friday, 11:00 AM", 6, listOf("jordan.wu@univ.edu", "liam.smith@univ.edu", "clara.diaz@univ.edu"))
        )

        // Pre-populate Chat Messages
        _messages.value = listOf(
            ChatMessage(channelId = "all", senderName = "System", senderEmail = "admin@univ.edu", content = "Welcome to the general announcements channel! Admins can broadcast messages in real-time.", timestamp = System.currentTimeMillis() - 86400000),
            ChatMessage(channelId = "cs_club", senderName = "Ethan Walker", senderEmail = "ethan.walker@univ.edu", content = "Hey everyone! Who is up for the hackathon this weekend? We need one more developer for our team.", timestamp = System.currentTimeMillis() - 7200000),
            ChatMessage(channelId = "cs_club", senderName = "Sarah Lee", senderEmail = "sarah.lee@univ.edu", content = "I am interested! I mostly write Kotlin and Compose. Can I join?", timestamp = System.currentTimeMillis() - 3600000),
            ChatMessage(channelId = "debate", senderName = "Sophia G.", senderEmail = "sophia.g@univ.edu", content = "Reminder: Today's debate is on AI regulation. 6:00 PM in Seminar Room 2.", timestamp = System.currentTimeMillis() - 14400000)
        )

        // Pre-populate student users list for Moderation/Analytics
        _users.value = listOf(
            StudentUser("1", "Ethan Walker", "ethan.walker@univ.edu", "ID-2024-9102", "student", false, "https://images.unsplash.com/photo-1539571696357-5a69c17a67c6?q=80&w=200&auto=format&fit=crop"),
            StudentUser("2", "Sarah Lee", "sarah.lee@univ.edu", "ID-2024-8114", "student", false, "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=200&auto=format&fit=crop"),
            StudentUser("3", "Clara Diaz", "clara.diaz@univ.edu", "ID-2025-4491", "student", false, "https://images.unsplash.com/photo-1438761681033-6461ffad8d80?q=80&w=200&auto=format&fit=crop"),
            StudentUser("4", "Jordan Wu", "jordan.wu@univ.edu", "ID-2024-3882", "student", false, "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200&auto=format&fit=crop"),
            StudentUser("5", "Liam Smith", "liam.smith@univ.edu", "ID-2025-7721", "student", false, "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?q=80&w=200&auto=format&fit=crop"),
            StudentUser("6", "Sophia G.", "sophia.g@univ.edu", "ID-2023-1102", "student", true, "https://images.unsplash.com/photo-1544005313-94ddf0286df2?q=80&w=200&auto=format&fit=crop"),
            StudentUser("7", "Abdullah Ayodele", "abdullahayodele2506@gmail.com", "ID-ADMIN-2506", "admin", false, "")
        )
    }

    private fun loadLocalSession() {
        val email = sharedPrefs.getString("user_email", null)
        val name = sharedPrefs.getString("user_name", null)
        val studentId = sharedPrefs.getString("user_studentId", null)
        val role = sharedPrefs.getString("user_role", null)

        if (email != null && name != null) {
            _currentUser.value = StudentUser(
                id = sharedPrefs.getString("user_id", UUID.randomUUID().toString())!!,
                name = name,
                email = email,
                studentId = studentId ?: "ID-PORTAL",
                role = role ?: if (email.equals("abdullahayodele2506@gmail.com", true)) "admin" else "student",
                isModerated = false
            )
            Log.d("CampusRepository", "Session restored: ${name} (${email}) as ${role}")
        }
    }

    private fun saveLocalSession(user: StudentUser) {
        sharedPrefs.edit()
            .putString("user_id", user.id)
            .putString("user_email", user.email)
            .putString("user_name", user.name)
            .putString("user_studentId", user.studentId)
            .putString("user_role", user.role)
            .apply()
    }

    private fun clearLocalSession() {
        sharedPrefs.edit().clear().apply()
    }

    // --- Authentication ---
    fun signInWithStudentId(studentId: String, email: String, name: String): Result<StudentUser> {
        if (studentId.isBlank() || email.isBlank() || name.isBlank()) {
            return Result.failure(Exception("All sign-in fields must be completed."))
        }

        val lowercaseEmail = email.trim().lowercase()
        val finalRole = if (lowercaseEmail == "abdullahayodele2506@gmail.com") "admin" else "student"

        val user = StudentUser(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            email = lowercaseEmail,
            studentId = studentId.trim().uppercase(),
            role = finalRole
        )

        // If in Sandbox Mode, simulated sign-in
        _currentUser.value = user
        saveLocalSession(user)

        // Sync to firebase if enabled
        if (isFirebaseEnabled) {
            try {
                firestore?.collection("users")?.document(user.id)?.set(user)
            } catch (e: Exception) {
                Log.e("CampusRepository", "Firebase Firestore failed to persist user", e)
            }
        }

        // Add to our users list if not present
        if (!_users.value.any { it.email.lowercase() == lowercaseEmail }) {
            _users.value = _users.value + user
        }

        return Result.success(user)
    }

    fun signInExistingUser(studentId: String, email: String): Result<StudentUser> {
        val lowercaseEmail = email.trim().lowercase()
        val cleanedStudentId = studentId.trim().uppercase()

        val existingUser = _users.value.find {
            it.email.lowercase() == lowercaseEmail && it.studentId.uppercase() == cleanedStudentId
        }

        if (existingUser != null) {
            _currentUser.value = existingUser
            saveLocalSession(existingUser)
            return Result.success(existingUser)
        } else {
            return Result.failure(Exception("No registered account matches that Email & Student ID. Please switch to 'Register' tab to create a new account."))
        }
    }

    fun signInWithGoogleSimulated(email: String, name: String): Result<StudentUser> {
        val finalRole = if (email.trim().lowercase() == "abdullahayodele2506@gmail.com") "admin" else "student"
        val user = StudentUser(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email.trim().lowercase(),
            studentId = "GG-${(100000..999999).random()}",
            role = finalRole
        )
        _currentUser.value = user
        saveLocalSession(user)

        if (!_users.value.any { it.email.lowercase() == email.trim().lowercase() }) {
            _users.value = _users.value + user
        }

        return Result.success(user)
    }

    fun signOut() {
        _currentUser.value = null
        clearLocalSession()
        if (isFirebaseEnabled) {
            try {
                firebaseAuth?.signOut()
            } catch (e: Exception) {
                Log.e("CampusRepository", "Firebase auth signout failed", e)
            }
        }
    }

    // --- Class Schedules ---
    fun addSchedule(schedule: ClassSchedule) {
        _schedules.value = _schedules.value + schedule
        if (isFirebaseEnabled) {
            firestore?.collection("schedules")?.document(schedule.id)?.set(schedule)
        }
    }

    fun deleteSchedule(id: String) {
        _schedules.value = _schedules.value.filter { it.id != id }
        if (isFirebaseEnabled) {
            firestore?.collection("schedules")?.document(id)?.delete()
        }
    }

    // --- Grades Tracker ---
    fun addGradeCourse(course: GradeCourse) {
        _grades.value = _grades.value + course
        if (isFirebaseEnabled) {
            firestore?.collection("grades")?.document(course.id)?.set(course)
        }
    }

    fun deleteGradeCourse(id: String) {
        _grades.value = _grades.value.filter { it.id != id }
        if (isFirebaseEnabled) {
            firestore?.collection("grades")?.document(id)?.delete()
        }
    }

    fun calculateGPA(): Double {
        val list = _grades.value
        if (list.isEmpty()) return 0.0
        var totalPoints = 0.0
        var totalCredits = 0
        for (course in list) {
            totalPoints += course.gradeValue * course.credits
            totalCredits += course.credits
        }
        return if (totalCredits > 0) totalPoints / totalCredits else 0.0
    }

    // --- Study Partners ---
    fun createStudyRequest(courseCode: String, topic: String, location: String, time: String, sizeLimit: Int) {
        val user = _currentUser.value ?: return
        val req = StudyRequest(
            studentName = user.name,
            studentEmail = user.email,
            courseCode = courseCode,
            topic = topic,
            location = location,
            time = time,
            sizeLimit = sizeLimit,
            currentMembers = listOf(user.email)
        )
        _studyRequests.value = _studyRequests.value + req
        if (isFirebaseEnabled) {
            firestore?.collection("study_requests")?.document(req.id)?.set(req)
        }
    }

    fun joinStudyRequest(id: String) {
        val user = _currentUser.value ?: return
        _studyRequests.value = _studyRequests.value.map { req ->
            if (req.id == id && !req.currentMembers.contains(user.email) && req.currentMembers.size < req.sizeLimit) {
                val updatedMembers = req.currentMembers + user.email
                val updatedReq = req.copy(currentMembers = updatedMembers)
                if (isFirebaseEnabled) {
                    firestore?.collection("study_requests")?.document(req.id)?.set(updatedReq)
                }
                updatedReq
            } else req
        }
    }

    fun leaveStudyRequest(id: String) {
        val user = _currentUser.value ?: return
        _studyRequests.value = _studyRequests.value.map { req ->
            if (req.id == id && req.currentMembers.contains(user.email)) {
                val updatedMembers = req.currentMembers.filter { it != user.email }
                val updatedReq = req.copy(currentMembers = updatedMembers)
                if (isFirebaseEnabled) {
                    firestore?.collection("study_requests")?.document(req.id)?.set(updatedReq)
                }
                updatedReq
            } else req
        }
    }

    // --- Messaging ---
    fun sendMessage(channelId: String, content: String) {
        val user = _currentUser.value ?: return
        val msg = ChatMessage(
            channelId = channelId,
            senderName = user.name,
            senderEmail = user.email,
            content = content
        )
        _messages.value = _messages.value + msg
        if (isFirebaseEnabled) {
            firestore?.collection("messages")?.document(msg.id)?.set(msg)
        }
    }

    // --- Admin Dashboard Announcements ---
    fun broadcastAnnouncement(title: String, content: String, isHighPriority: Boolean) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val dateStr = sdf.format(Date())
        val announcement = Announcement(
            title = title,
            content = content,
            date = dateStr,
            author = _currentUser.value?.name ?: "Campus Admin",
            isHighPriority = isHighPriority
        )
        _announcements.value = listOf(announcement) + _announcements.value // pre-pend new ones
        if (isFirebaseEnabled) {
            firestore?.collection("announcements")?.document(announcement.id)?.set(announcement)
        }
    }

    fun deleteAnnouncement(id: String) {
        _announcements.value = _announcements.value.filter { it.id != id }
        if (isFirebaseEnabled) {
            firestore?.collection("announcements")?.document(id)?.delete()
        }
    }

    // --- Admin Moderation ---
    fun toggleUserModeration(userId: String) {
        _users.value = _users.value.map { user ->
            if (user.id == userId) {
                val updatedUser = user.copy(isModerated = !user.isModerated)
                if (isFirebaseEnabled) {
                    firestore?.collection("users")?.document(userId)?.set(updatedUser)
                }
                updatedUser
            } else user
        }
    }

    // --- Static Singleton pattern ---
    companion object {
        @Volatile
        private var INSTANCE: CampusRepository? = null

        fun getInstance(context: Context): CampusRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = CampusRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
