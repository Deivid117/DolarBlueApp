package com.varqulabs.dolarblue.history.presentation.components.auxiliar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.varqulabs.dolarblue.R

val FavoriteIconPositive: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.favoritos_1_p)

val FavoriteIconNegative: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.favoritos_1_n)

val BorderFavoriteIconPositive: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.favoritos_2_p)

val BorderFavoriteIconNegative: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.favoritos_2_n)

val SearchIconPositive: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id =  R.drawable.buscar_p)

val SearchIconNegative: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id =  R.drawable.buscar_n)

val ClearIconPositive: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id =  R.drawable.borrar_p)

val ClearIconNegative: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id =  R.drawable.borrar_n)

val EditIconNegative: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.editar_p)

val EditIconPositive: ImageVector
    @Composable
    get() = ImageVector.vectorResource(id = R.drawable.editar_n)