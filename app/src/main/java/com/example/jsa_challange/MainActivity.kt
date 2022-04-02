package com.example.jsa_challange

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.MutableInt
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jsa_challange.ui.theme.JSA_ChallangeTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.ContextCompat.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var userInfoList = mutableStateListOf<Repo>()

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JSA_ChallangeTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.setStatusBarColor(MaterialTheme
                    .colorScheme.primary)
                systemUiController.setNavigationBarColor(MaterialTheme.colorScheme.onBackground)
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    GetScaffold()
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun GetScaffold(){
    Scaffold(
        topBar = {TopBar()},
        content = {MainContent()},
        containerColor = MaterialTheme.colorScheme.background
    )
}

@Composable
fun MainContent(){
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ){
        Column() {
            Box(
                Modifier
                    .weight(1f)) {
                UserRecyclerView(userInfoList)
            }
            Box() {
                SearchView()
            }
        }
    }
}

@Composable
fun SearchView(
    placeholderText: String = "Search",
    context: Context = LocalContext.current
) {

    fun fetchRepos(user: String){
        val response = Retrofit(user).repos
        response.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>?) {
                if (response != null && response.isSuccessful) {
                    println("I found some ting")
                    var repos : List<Repo>? = response.body()
                    userInfoList.swapList(repos)
                }else{
                    Toast.makeText(
                        context,
                        "No ting there",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Repo>>?, t: Throwable?) {
                Toast.makeText(
                    context,
                    "ERROR: Some ting wong with service",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    var text by rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .clip(shape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
            .background(MaterialTheme.colorScheme.onBackground),
        contentAlignment = Alignment.Center
    ){
        Row(verticalAlignment = Alignment.CenterVertically) {

            Button(onClick = {
                if(text != ""){
                    fetchRepos(text)
                }
                             },
                modifier = Modifier
                    .padding(5.dp, 0.dp, 0.dp, 5.dp)
            ){ Text(text = "Fetch Repos") }

            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                    if(text != ""){
                        //Make instant search without pressing button (In API restriction on the
                        // ammount of the requests, So I deactivated It)
                        //fetchRepos(text)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(shape = RoundedCornerShape(100.dp))
                    .align(Alignment.CenterVertically),
                textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 0.dp, 5.dp)
                            .clip(shape = RoundedCornerShape(100.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .align(Alignment.CenterVertically),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(15.dp, 0.dp, 0.dp, 0.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (text.isEmpty())
                                Text(
                                placeholderText,
                                style = LocalTextStyle.current.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            )
                            innerTextField()
                        }
                        IconButton(
                            onClick = { text = ""
                                      userInfoList.clear()
                                      },
                            modifier = Modifier
                                .size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(20.dp)
                            )
                        }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListItem(user : Repo){
    var context = LocalContext.current
    Card(
        onClick = {
            val openURL = Intent(Intent.ACTION_VIEW, Uri.parse(user.html_url))
            startActivity(context, openURL, null)
        },
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp),
        shape = RoundedCornerShape(25.dp)
    ) {
        Row() {
            //Potential place for images
            var isExpanded by remember { mutableStateOf(false) }
            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = user.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                )
                if (user.description != null){
                    Spacer(modifier = Modifier.height(15.dp))
                    Card(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                    ) {
                        Surface(
                            modifier = Modifier
                                .animateContentSize(),
                            color = Color.Transparent
                        ) {
                            var description = remember { SnapshotStateList<String>() }

                            if (!isExpanded){
                                description.swapList(listOf("Press here for details"))
                            }else{
                                description.swapList(listOf(user.description))
                            }
                            Text(
                                text = description.get(0),
                                modifier = Modifier.padding(7.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun UserRecyclerView(userList : SnapshotStateList<Repo>){
    LazyColumn(
        contentPadding = PaddingValues(5.dp,5.dp),
    ){
        items(
            items = userList,
            itemContent = {
                UserListItem(user = it)
            },
        )
    }
}

fun <T>SnapshotStateList<T>.swapList(newList: List<T>?){
    clear()
    addAll(newList!!)
}

@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Git User Repo List",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(androidx.compose.material3
                .MaterialTheme.colorScheme.primary),
        modifier = Modifier.clip(shape =
            RoundedCornerShape(0.dp,0.dp,25.dp,25.dp))
    )
}

@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreview() {
    GetScaffold()
}