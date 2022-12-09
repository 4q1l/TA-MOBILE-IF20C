package com.example.tugasakhir

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tugasakhir.ui.theme.TugasakhirTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : ComponentActivity() {

    companion object {
        const val RC_SIGN_IN = 100
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit  var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("329171652832-h19pdrag5p1ur43uehcvdu1qkarab743.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        setContent {
            TugasakhirTheme {

                if (mAuth.currentUser == null)
                {
                    GoogleSignInButton()
                    {
                        signIn()
                    }
                }
                else
                {
                    val user: FirebaseUser = mAuth.currentUser!!
                    profileScreen(
                        name = user.displayName!!,
                        email = user.email!!,
                        signOutClicked = {
                            signOut()
                        }
                    )
                }

                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Scaffold()
                }
            }
        }
    }

    private fun profileScreen(name: String, email: String, signOutClicked: () -> Unit) {

    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

//    private fun signIn() {
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception

            if(task.isSuccessful)
            {
                try
                {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                }
                catch (e: Exception)
                {
                    Log.d("SignIn", "Login Gagal")
                }
            }
            else
            {
                Log.d("SignIn", exception.toString())
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this)
            {
                    task ->
                if (task.isSuccessful)
                {
                    Toast.makeText(this, "SignIn Berhasil", Toast.LENGTH_SHORT).show()
                    setContent {
                        TugasakhirTheme {
                            val user: FirebaseUser = mAuth.currentUser!!
                            profileScreen(
                                name = user.displayName!!,
                                email = user.email!!,
                                signOutClicked = {
                                    signOut()
                                }
                            )
                        }
                    }
                }
                else
                {
                    Toast.makeText(this, "SignIn Gagal", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signOut() {
        // get google account
        val googleSignInClient: GoogleSignInClient

        // configure google signin
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("329171652832-h19pdrag5p1ur43uehcvdu1qkarab743.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)

        // sign out of all account
        mAuth.signOut()
        googleSignInClient.signOut().addOnSuccessListener {
            Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show()
            setContent{
                TugasakhirTheme {
                    GoogleSignInButton {
                        signIn()
                    }
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Logout gagal!",Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun GoogleSignInButton(
    signInClicked: () -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .padding(30.dp)
                .height(55.dp)
                .fillMaxWidth()
                .clickable { signInClicked() },
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(width = 2.dp, color = Color.Red),
            elevation = 5.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .size(30.dp)
                        .align(CenterVertically),
                    painter = painterResource(R.drawable.google_logo),
                    contentDescription = "google logo")
                Text(
                    text = "Masuk dengan Google",
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .align(CenterVertically))
            }

        }
    }
}

@Composable
fun Body(){
    Row(modifier = Modifier.fillMaxSize(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center){
        Text(text = "Body")
    }
}

@Composable
fun profilescreen(
    name : String,
    email : String,
    signOutClicked: () -> Unit
){
    Column(modifier = Modifier.padding(12.dp)) {
        OutlinedTextField(value = name, onValueChange = {}, readOnly = true, label = { Text(text = "Nama")})
        OutlinedTextField(value = email, onValueChange = {}, readOnly = true, label = { Text(text = "Email")})
        Button(
            onClick = { signOutClicked },
            modifier = Modifier
                .padding(12.dp)
                .align(CenterHorizontally))
        {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun Scaffold() {
    // Memanggil semua elemen ke dalam scaffold
    Scaffold(
        topBar = { TopBar() },
        content = { Body() },
        bottomBar = { nav() }

    )
}

@Composable
fun TopBar() {
    var texInput by remember {
        mutableStateOf(TextFieldValue())
    }

    Row(
        modifier = Modifier
            .background(Color.Cyan)
            .padding(10.dp)
            .fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row {
            Icon(Icons.Filled.Menu, contentDescription = "Menu atas")
        }

        Row(Modifier.padding(start = 16.dp, end = 16.dp)) {
            TextField(value = texInput, onValueChange = {texInput = it})
        }

        Row {
            Icon(Icons.Filled.AccountCircle, contentDescription = "Menu atas")
        }

    }
}



@Composable
fun nav(){
    Row(modifier = Modifier
        .background(Color.Cyan)
        .padding(10.dp)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = CenterVertically
    ) {

        Column(horizontalAlignment = CenterHorizontally){
            Icon(Icons.Filled.Home, contentDescription = "Home")
            Text(text = "Home")
        }

        Column(horizontalAlignment = CenterHorizontally){
            Icon(Icons.Filled.Notifications, contentDescription = "Home")
            Text(text = "Notif")
        }

        Column(horizontalAlignment = CenterHorizontally){
            Icon(Icons.Filled.Settings, contentDescription = "Home")
            Text(text = "Setting")
        }

    }
}


//@Preview(showBackground = true)
//@Composable
//fun Pertemuan1(){
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Row(modifier = Modifier.padding(20.dp)) {
//            Image(
//                painter = painterResource(id = R.drawable.dog),
//                contentDescription = "dog"
//            )
//        }
//
//        Row() {
//            Column {
//                Text(text = "Dog",
//                fontSize = 50.sp)
//            }
//        }
//
//        Row() {
//            Column {
//                Text(text = "Anak Anjing")
//            }
//        }
//
//        Row(modifier = Modifier.padding(top = 10.dp)) {
//            Column() {
//                androidx.compose.material.Icon(Icons.Filled.Email, contentDescription = "Email")
//            }
//            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
//                Text(text = "dog@gmail.com")
//            }
//        }
//
//        Row() {
//            Column() {
//                androidx.compose.material.Icon(Icons.Filled.Email, contentDescription = "Email")
//            }
//            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
//                Text(text = "dog@gmail.com")
//            }
//        }
//        Row() {
//            Column() {
//                androidx.compose.material.Icon(Icons.Filled.Email, contentDescription = "Email")
//            }
//            Column(modifier = Modifier.padding(horizontal = 10.dp)) {
//                Text(text = "dog@gmail.com")
//            }
//        }
//    }
//}