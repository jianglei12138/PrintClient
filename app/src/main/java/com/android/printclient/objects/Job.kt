package com.android.printclient.objects

class Job {
    var id: Int = 0
    var dest: String? = null
    var title: String? = null
    var user: String? = null
    var format: String? = null
    var state: Int = 0
    var size: Int = 0
    var priority: Int = 0
    var completed_time: Long = 0
    var creation_time: Long = 0
    var processing_time: Long = 0
}