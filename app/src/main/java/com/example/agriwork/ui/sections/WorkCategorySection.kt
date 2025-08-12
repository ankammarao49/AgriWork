import android.R.attr.rotation
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.agriwork.ui.components.CategoryCard
import com.example.agriwork.ui.theme.Poppins

@Composable
fun WorkCategorySection(
    title: String,
    categoryItems: List<Pair<String, Int>>,
    onCategoryClick: (String) -> Unit
) {
    var showAll by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (showAll) 180f else 0f, label = "")

    val categoriesToShow = if (showAll) categoryItems else categoryItems.take(4)

    Column {
        Text(title, fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,)
        Spacer(modifier = Modifier.height(15.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 2 per row
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = if (showAll) 1000.dp else 300.dp), // height limit when collapsed
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            userScrollEnabled = false // disable scroll so parent can scroll
        ) {
            items(categoriesToShow) { (category, imageResId) ->
                CategoryCard(
                    category = category,
                    image = painterResource(id = imageResId)
                ) {
                    onCategoryClick(category)
                }
            }
        }

        OutlinedButton(
            onClick = { showAll = !showAll },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(50) // pill shape
        ) {
            Icon(
                imageVector = Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .rotate(rotation)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (showAll) "Show Less" else "Show More",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
        }
    }
}
