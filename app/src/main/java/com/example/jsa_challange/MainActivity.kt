package com.example.jsa_challange

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.core.content.ContextCompat.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// I have added material3 library in order to make app to have a good modern look as well as to
// include some of the latest new features such as colorScheme that uses MaterialYou making
// Application's design to adapt to user's color pallet of their wallpapers. (Android 12+), but
// if user has Android version less than 12 it will use application's own color's

// In addition well application uses light/dark theme adapting it even more for user's preference.

// I have also gave the application a modern look to it trying to make it look as good
// as I can by adding animations and rounded corners and more of other design changes such as
// colors and sizes.

// Var that holds user's Info for later use in LazyColumn. Thanks to Compose Lazy column can
// easily update list in real time once the list has been changed.

private var userInfoList = mutableStateListOf<Repo>()

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JSA_ChallangeTheme {

                // Implemented a library for easy change of the system UI elements such as colors
                // and visibility.

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

// TopBar, MainContent have been put into one Scaffold for easier use.

@ExperimentalComposeUiApi
@ExperimentalMaterial3Api
@Composable
fun GetScaffold(){
    Scaffold(
        topBar = {TopBar()},
        content = {MainContent()},
        containerColor = MaterialTheme.colorScheme.background
    )
}

// MainContent contains ReposRecyclerList (RecyclerView/LazyColumn) and SearchView, Both have
// been put into Boxes as in Compose modifier "Weight" can be reached only if it's the container
// that is located inside of a Column/Row.

// Weight is required for the LazyColumn to not push SearchView all the way down making it
// impossible to reach it once the user's repos list makes LazyColumn stretch higher than phone's
// screen.

@ExperimentalComposeUiApi
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
                ReposRecyclerView(userInfoList)
            }
            Box() {
                SearchView()
            }
        }
    }
}

// I made custom SearchView in order to be able to resize it's height to make button and SearchView
// have the same height and make them both look ergonomic and appealing to the user's eye.

@ExperimentalComposeUiApi
@Composable
fun SearchView(

    // PlaceHolder works as a placeholder to hint to the user where to type.

    placeholderText: String = stringResource(R.string.place_holder_text),
    context: Context = LocalContext.current
) {

    // fetchRepos works as a command that updates userInfoList that contains links details and
    // names of the Repos that it will receive upon request to the server.

    val keyboardController = LocalSoftwareKeyboardController.current

    fun fetchRepos(user: String){
        keyboardController?.hide()
        val response = Retrofit(user).repos
        response.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>?, response: Response<List<Repo>>?) {
                if (response != null && response.isSuccessful) {
                    var repos : List<Repo>? = response.body()
                    userInfoList.swapList(repos)
                }else{

                    // If there is no such user then associated Toast message will popup for the user in oder to
                    // let the person know that search can't find anything with that name.

                    Toast.makeText(
                        context,
                        "No ting there",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            // In case if request fails phone will display a Toast that there's something wrong
            // with the service.

            override fun onFailure(call: Call<List<Repo>>?, t: Throwable?) {
                Toast.makeText(
                    context,
                    "ERROR: Some ting wong with service",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    // Var of the text that user types into the TextField.

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

            // Button that upon pressing will call function fetchRepos and will update the
            // userInfoList therefore updating LazyColumn that displays user's information.


            Button(onClick = {
                if(text != ""){
                    fetchRepos(text)
                }
                             },
                modifier = Modifier
                    .padding(5.dp, 0.dp, 0.dp, 5.dp)
            ){ Text(text = stringResource(R.string.search_button_text)) }


            BasicTextField(
                value = text,
                onValueChange = {
                    text = it
                    if(text != ""){

                        // I have made live search without pressing button but (In the API there
                        // are restriction on the amount of the requests, So I deactivated It).

                        //fetchRepos(text)

                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
                    .clip(shape = RoundedCornerShape(100.dp))
                    .align(Alignment.CenterVertically),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp),
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

                        // An IconButton that is being used as a "clear button" when user enters
                        // text, upon pressing, it will instantly erase all text that user has
                        // entered.

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
                },

                // Hiding keyboard on pressing "done" button

                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        fetchRepos(text)
                    }
                )
            )
        }
    }
}

// UserItemList is being used as a placeholder for user's repo's information.
// It contains Repo's name, Repo's description.
// I as well have implemented animations in order to make visuals to be more pleasant for
// user's eye.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListItem(user : Repo){
    var context = LocalContext.current

    // Initial Card placeholder has been given an "onClick" parameter and inside of it, it upon
    // clicking it starts new activity that has been passed a Repo's URL and will forward user to
    // the browser opening Repo's page.

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

            // Row has been added in case I'd need to put something perpendicular to the name and
            // description such as link button or such.

            // isExpanded Var has been added in order to know if the description field has
            // been expanded or not.

            var isExpanded by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .padding(15.dp)
                    .align(Alignment.CenterVertically)
            ) {

                // Repo's name

                Text(text = user.name,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                )

                // Sometimes Repo's might not have any description to them and API will parse
                // null to the list's description Var. So, for these situations if the Repo
                // doesn't has description it won't have any clickable Card for expansion nor
                // description leaving only name on the Card.

                if (user.description != null){
                    Spacer(modifier = Modifier.height(15.dp))
                    Card(
                        containerColor = MaterialTheme.colorScheme.secondary,

                        // if description's Card has been clicked then it changes isExpanded's
                        // value onto the opposite one.

                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }
                    ) {

                        // In order to make animations smooth and work properly Surface has been
                        // added and made transparent, as a holder for text inside of the Card.
                        // So when text changes in size, all containers underneath Surface would
                        // have smooth animations upon expanding.

                        Surface(
                            modifier = Modifier
                                .animateContentSize(),
                            color = Color.Transparent
                        ) {

                            // mutable description Var has been added in order to change text
                            // inside of the 'Text', instead of creating a new one each time
                            // user expands and shrinks the description.

                            var description = remember { SnapshotStateList<String>() }

                            // if description has been clicked then it changes description's text
                            // from "hint text" to the description text and vice versa.

                            if (!isExpanded){
                                description.swapList(listOf(stringResource(R.string.details_button)))
                            }else{
                                description.swapList(listOf(user.description))
                            }

                            // Description text.

                            Text(
                                text = description.get(0),
                                modifier = Modifier
                                    .padding(7.dp)
                                    .animateContentSize(),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// ReposRecyclerView is being used as a place holding all placeholders with Repo's information
// inside of them that are being put into the LazyColumn.

@Composable
fun ReposRecyclerView(userList : SnapshotStateList<Repo>){
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

// function making it easier to swap list of mutableList with the one that you need it to have.

fun <T>SnapshotStateList<T>.swapList(newList: List<T>?){
    clear()
    addAll(newList!!)
}

// TopBar was created in order to add an AppBar and align and arrange all elements inside of
// it, as well as make all modifications to it's appearance.

@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.top_bar_text),
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

// a Preview composable have been made in order to track all changes to the UI of the
// Application, but it broke once I added "systemUiController" library as for some reason it was
// breaking it's sandbox or some sort's disabling real time updating e.t.c

@ExperimentalComposeUiApi
@Preview
@ExperimentalMaterial3Api
@Composable
fun DefaultPreview() {
    GetScaffold()
}