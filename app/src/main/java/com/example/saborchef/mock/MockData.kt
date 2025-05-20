package com.example.saborchef.mock

import com.example.saborchef.R
import com.example.saborchef.model.Recipe
import com.example.saborchef.model.Step

val sampleRecipes = listOf(
    Recipe(
        id       = "1",
        title    = "Ensalada caprese",
        user     = "usuario88",
        rating   = 4,
        duration = "15 min",
        portions = "2 porciones",
        images   = listOf(
            R.drawable.img_ratatouille,
            R.drawable.img_ratatouille,
            R.drawable.img_ratatouille
        ),
        ingredients = listOf(
            "Tomates: 2 un.",
            "Queso mozzarella: 150 gr.",
            "Hojas de albahaca",
            "Sal y aceite de oliva"
        ),
        steps = listOf(
            Step(
                description = "Lavar muy bien y secar los tomates. Cortarlos en rodajas grandes.",
                media = listOf(
                    R.drawable.img_step1,
                    R.drawable.img_step1
                )
            ),
            Step(
                description = "Lavar la albahaca y dejar algunas hojas enteras; el resto picarla finamente.",
                media = listOf(
                    R.drawable.img_step1,
                    R.drawable.img_step1,
                    R.drawable.img_step1,
                    R.drawable.img_step1
                )
            ),
            Step(
                description = "Alternar rodajas de tomate y mozzarella en un plato, agregar albahaca.",
                media = listOf(
                    R.drawable.img_step1
                )
            )
        ),
        comments = listOf(
            "Carla Rodríguez" to "Muy buena receta!!",
            "Agustin89"      to "Es rápida y económica, me gusta"
        )
    ),
    // Repetimos la misma estructura en las otras 2 recetas de prueba
    Recipe(
        id       = "2",
        title    = "Pasta Alfredo",
        user     = "chefMario",
        rating   = 2,
        duration = "25 min",
        portions = "3 porciones",
        images   = listOf(
            R.drawable.img_spaghetti,
            R.drawable.img_spaghetti
        ),
        ingredients = listOf(
            "Pasta: 200 gr.",
            "Crema: 100 ml",
            "Queso parmesano: 50 gr.",
            "Mantequilla: 20 gr."
        ),
        steps = listOf(
            Step(
                description = "Cocinar la pasta en agua con sal hasta que esté al dente.",
                media = listOf(R.drawable.img_spaghetti)
            ),
            Step(
                description = "Derretir mantequilla, agregar crema y queso, mezclar con la pasta.",
                media = listOf(R.drawable.img_spaghetti)
            )
        ),
        comments = listOf(
            "LauraG"   to "Deliciosa, perfecta para cenar",
            "Pepito42" to "Me faltó un toque de sal"
        )
    ),
    Recipe(
        id       = "3",
        title    = "Tarta de Manzana",
        user     = "casero1",
        rating   = 3,
        duration = "1 h",
        portions = "6 porciones",
        images   = listOf(
            R.drawable.img_spaghetti,
            R.drawable.img_spaghetti,
            R.drawable.img_spaghetti
        ),
        ingredients = listOf(
            "Manzanas: 4 un.",
            "Harina: 200 gr.",
            "Azúcar: 100 gr.",
            "Mantequilla: 100 gr."
        ),
        steps = listOf(
            Step(
                description = "Pelá y cortá las manzanas en láminas finas.",
                media = listOf(R.drawable.img_spaghetti)
            ),
            Step(
                description = "Mezclá harina, azúcar y mantequilla hasta formar masa.",
                media = listOf(R.drawable.img_spaghetti)
            ),
            Step(
                description = "Forramos el molde, agregamos manzanas, horneamos 40 minutos.",
                media = listOf(R.drawable.img_spaghetti)
            )
        ),
        comments = listOf(
            "AnaP" to "¡Me encantó la textura!",
            "Luis98" to "Quedó perfecta, gracias por compartir"
        )
    )
)
