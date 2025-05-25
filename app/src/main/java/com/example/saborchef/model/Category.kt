import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color

enum class Category(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    SNACKS("Snacks", Icons.Default.Fastfood, Color(0xFFFFA726)),
    POSTRES("Postres", Icons.Default.Cake, Color(0xFFBA68C8)),
    VEGANO("Vegano", Icons.Default.Spa, Color(0xFF81C784)),
    CARNES("Carnes", Icons.Default.SetMeal, Color(0xFFE57373)),
    BEBIDAS("Bebidas", Icons.Default.LocalDrink, Color(0xFF64B5F6))
}
