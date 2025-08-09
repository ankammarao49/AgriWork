import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.agriwork.ui.components.CategoryCard
import com.example.agriwork.ui.theme.Poppins

@Composable
fun WorkCategorySection(
    title: String,
    categoryItems: List<Pair<String, Int>>,
    onCategoryClick: (String) -> Unit
) {
    var showAll by remember { mutableStateOf(false) }

    val categoriesToShow = if (showAll) categoryItems else categoryItems.take(4)

    Column {
        Text(title, fontFamily = Poppins, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(10.dp))

        categoriesToShow.forEach { (category, imageResId) ->
            CategoryCard(category = category, image = painterResource(id = imageResId)) {
                onCategoryClick(category)
            }
        }

        OutlinedButton(
            onClick = { showAll = !showAll },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp),
//            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF113F67)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Text(if (showAll) "Show Less" else "See More")
        }
    }
}
