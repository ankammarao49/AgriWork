import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.agriwork.data.model.AppUser
import com.example.agriwork.data.model.Work
import com.example.agriwork.data.repository.WorkRepository.listenToWorks
import com.example.agriwork.data.utils.WorkFilterType
import com.example.agriwork.ui.sections.WorkListSection
import com.example.agriwork.ui.theme.Poppins
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FarmerDashboardScreen(currentUser: AppUser, navController: NavHostController) {
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val workList = remember { mutableStateListOf<Work>() }

    LaunchedEffect(Unit) {
        listenToWorks { updatedWorks ->
            workList.clear()
            workList.addAll(updatedWorks)
            isLoading = false
        }
    }

    Column {
        Text("Your Posted Works", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))

        when {
            isLoading -> CircularProgressIndicator()
            error != null -> Text("Error: $error", color = Color.Red)
            else -> {
                val createdByMe = workList.filter { it.farmer.uid == currentUid }

                WorkListSection(
                    title = "Works You Posted",
                    workList = createdByMe,
                    currentUid = currentUid,
                    currentUserRole = "farmer",
                    filterType = WorkFilterType.CREATED_BY_ME,
                    navController = navController,
                    onApplyClick = { /* Farmers don’t apply, so no apply callback */ },
                )
            }
        }
    }
}