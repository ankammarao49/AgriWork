package com.example.agriwork.ui.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.agriwork.data.model.Work
import com.example.agriwork.data.utils.WorkFilterType
import com.example.agriwork.data.utils.filterWorks
import com.example.agriwork.data.utils.shouldShowApplyButton
import com.example.agriwork.ui.components.WorkShowCard
import com.example.agriwork.ui.theme.Poppins

@Composable
fun WorkListSection(
    title: String,
    workList: List<Work>,
    currentUid: String,
    currentUserRole: String,
    filterType: WorkFilterType,
    onApplyClick: (Work) -> Unit,
    navController: NavHostController
) {
    val filteredWorks = filterWorks(workList, currentUid, filterType)

    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(title, fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(15.dp))

        if (filteredWorks.isEmpty()) {
            Text("No work found in this section.")
        } else {
            filteredWorks.forEach { work ->
                val hasAlreadyApplied = work.workersApplied?.contains(currentUid) == true
                val isWorkFull = (work.workersSelected?.size ?: 0) >= work.workersNeeded
                val canApply = !hasAlreadyApplied && !isWorkFull

                WorkShowCard(
                    farmerName = work.farmer.name,
                    workTitle = work.workTitle,
                    daysRequired = work.daysRequired,
                    acres = work.acres,
                    workersNeeded = work.workersNeeded,
                    noOfWorkersSelected = work.workersSelected?.size ?: 0,
                    noOfWorkersApplied = work.workersApplied?.size ?: 0,
                    location = work.farmer.location,
                    showApplyButton = shouldShowApplyButton(
                        currentUserRole = currentUserRole,
                        currentUid = currentUid,
                        work = work,
                    ),
                    onApplyClick = { onApplyClick(work) },
                    onViewApplicantsClick = {
                        navController.navigate("applicants/${work.id}")
                    }

                )
            }
        }
    }
}