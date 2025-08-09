package com.example.agriwork.data.utils

import com.example.agriwork.data.model.Work

enum class WorkFilterType {
    ALL, AVAILABLE, APPLIED, CREATED_BY_ME, SELECTED
}

fun filterWorks(workList: List<Work>, currentUid: String, type: WorkFilterType): List<Work> {
    return when (type) {
        WorkFilterType.ALL -> workList
        WorkFilterType.AVAILABLE -> workList.filter {
            !(it.workersApplied?.contains(currentUid) == true ||
                    (it.workersSelected?.size ?: 0) >= it.workersNeeded)
        }
        WorkFilterType.APPLIED -> workList.filter { it.workersApplied?.contains(currentUid) == true }
        WorkFilterType.CREATED_BY_ME -> workList.filter { it.farmer.uid == currentUid }
        WorkFilterType.SELECTED -> workList.filter { it.workersSelected?.contains(currentUid) == true }
    }
}

fun shouldShowApplyButton(currentUserRole: String, currentUid: String, work: Work): Boolean {
    return currentUserRole != "farmer" &&
            work.farmer.uid != currentUid &&
            work.workersApplied?.contains(currentUid) != true &&
            (work.workersSelected?.size ?: 0) < work.workersNeeded
}
