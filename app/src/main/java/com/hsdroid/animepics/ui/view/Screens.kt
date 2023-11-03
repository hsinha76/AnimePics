package com.hsdroid.animepics.ui.view

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.hsdroid.animepics.R
import com.hsdroid.animepics.ui.theme.Background
import com.hsdroid.animepics.ui.theme.CardBg
import com.hsdroid.animepics.ui.theme.Green
import com.hsdroid.animepics.ui.theme.Purple40
import com.hsdroid.animepics.ui.theme.TopBarColor
import com.hsdroid.animepics.ui.viewmodel.DataViewmodel
import com.hsdroid.animepics.utils.APIState
import com.hsdroid.animepics.utils.Helper
import com.hsdroid.animepics.utils.Helper.Companion.capitalizeFirstLetter
import com.kpstv.compose.kapture.attachController
import com.kpstv.compose.kapture.rememberScreenshotController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavHostController) {
    val categoryList = listOf("cry", "dance", "happy", "wink", "smile")
    Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Background, topBar = {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(id = R.string.app_name), color = Color.White
                )
            }, colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = TopBarColor)
        )
    }) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding()
        ) {

            val (text, spacerTop, spacerMid, lazyGrid) = createRefs()

            Spacer(modifier = Modifier
                .height(200.dp)
                .constrainAs(spacerTop) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                })

            Text(
                text = "Select a Tag to continue",
                fontSize = 20.sp,
                fontStyle = FontStyle.Normal,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .fillMaxWidth()
                    .constrainAs(text) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(spacerTop.bottom)
                    },
                color = Color.Black,
            )

            Spacer(modifier = Modifier
                .height(120.dp)
                .constrainAs(spacerMid) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(text.bottom)
                })

            LazyVerticalGrid(columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 150.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .constrainAs(lazyGrid) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(spacerMid.bottom)
                        bottom.linkTo(parent.bottom)
                    }) {
                items(categoryList) {
                    CardLayout(it, navController)
                }
            }
        }
    }
}

@Composable
fun CardLayout(tags: String, navController: NavHostController) {
    Card(
        modifier = Modifier
            .height(190.dp)
            .padding(8.dp)
            .clickable {
                navController.navigate("image_screen/$tags")
            }, elevation = CardDefaults.cardElevation(8.dp), shape = CircleShape
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CardBg),
            contentAlignment = Alignment.Center
        ) {
            Text(text = capitalizeFirstLetter(tags), fontSize = 16.sp, color = Color.White)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ImageScreen(
    selectedTag: String, dataViewModel: DataViewmodel, navController: NavHostController
) {

    val data = dataViewModel._response.collectAsState().value
    val imageUrl = remember {
        mutableStateOf("null")
    }
    val context = LocalContext.current

    when (data) {
        is APIState.SUCCESS -> imageUrl.value = data.data.url
        is APIState.FAILURE -> Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        else -> "do nothing"
    }

    val screenshotController = rememberScreenshotController()
    val galleryState = remember {
        mutableStateOf(false)
    }
    val resp = rememberAsyncImagePainter(model = imageUrl)

    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(title = {
            Text(
                text = capitalizeFirstLetter(selectedTag),
                Modifier.padding(horizontal = 4.dp),
                color = Color.White
            )
        },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = TopBarColor),
            navigationIcon = {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            navController.navigate("home")
                        })
            })
    }) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .height(500.dp)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {

                if (resp.state is AsyncImagePainter.State.Loading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                AsyncImage(
                    model = ImageRequest.Builder(context).data(Uri.parse(imageUrl.value))
                        .allowHardware(false).build(),
                    contentDescription = null,
                    modifier = Modifier
                        .wrapContentSize()
                        .defaultMinSize(400.dp)
                        .attachController(screenshotController)
                )

            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Button(
                    onClick = {
                        dataViewModel.callData(selectedTag)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(0.dp)
                        .weight(2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                ) {
                    Text(text = "Generate Next")
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        Modifier.padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            //do nothing scoped storage thing
                            galleryState.value = true
                        } else {
                            if (Helper.checkPermission(context)) {
                                galleryState.value = true
                            } else {
                                Helper.requestPermission(context)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(0.dp)
                        .weight(2f),
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    Text(text = "Save To Gallery", color = Color.White)
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        Modifier.padding(4.dp)
                    )
                }
            }
        }
    }

    //download state
    if (galleryState.value) {
        LaunchedEffect(key1 = true) {
            val bitmap = screenshotController.captureToBitmap()
            when (bitmap.isSuccess) {
                true -> {
                    Helper.toPublicDirectory(context, bitmap.getOrThrow())
                    Toast.makeText(context, "Saved To Gallery", Toast.LENGTH_SHORT).show()
                }

                else -> Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

            galleryState.value = false
        }
    }
}
