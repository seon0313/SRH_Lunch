package com.seon06.school_lunch_viewer

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.seon06.school_lunch_viewer.ui.theme.MyApplicationTheme
import com.seon06.school_lunch_viewer.ui.theme.Typography
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var loading_str = stringResource(R.string.loading_item)
                    var breakfast by remember { mutableStateOf(loading_str) }
                    var lunch by remember { mutableStateOf(loading_str) }
                    var dinner by remember { mutableStateOf(loading_str) }
                    var loadedDay by remember { mutableStateOf("...") }
                    var first by remember { mutableStateOf(false) }

                    val no_data = stringResource(R.string.error_no_data)

                    fun loadLunch(date: String = ""){
                        Thread {
                            Log.i("!","!!")
                            try {
                                var a = getLunch(date, no_data)
                                runOnUiThread{
                                    breakfast = a[0]
                                    lunch = a[1]
                                    dinner = a[2]
                                    loadedDay = a[3]

                                }

                            } finally {

                            }
                        }.start()
                    }
                    if (!first) {
                        loadLunch()
                        first = true;
                    }
                    mainMenu(breakfast, lunch, dinner, loadedDay, ::loadLunch)
                }
            }
        }
    }
}
@SuppressLint("NewApi")
fun getLunch(date: String="", no_data: String): Array<String> {
    var time = ""
    var get_date = ""
    var format = DateTimeFormatter.ofPattern("yyyyMMdd")
    var format2 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    if (date.equals("")) {
        time = LocalDateTime.now().format(format)
    } else {
        time = date
    }
    try {
        var URL = "https://open.neis.go.kr/hub/mealServiceDietInfo?Type=json&KEY=${BuildConfig.API_LUNCH}&MLSV_YMD=${time}&ATPT_OFCDC_SC_CODE=B10&SD_SCHUL_CODE=7010738";
        var url = URL(URL);
        val conn = url.openConnection() as HttpURLConnection;
        conn.requestMethod = "GET";
        conn.connectTimeout = 10000;
        conn.readTimeout = 10000;


        val data = conn.inputStream.bufferedReader().readText()
        val json = JSONObject(data.toString()).getJSONArray("mealServiceDietInfo").getJSONObject(1).getJSONArray("row")
        var breakfast = no_data
        var lunch = no_data
        var dinner = no_data

        for(i: Int in 0..json.length()-1){
            val item = json.getJSONObject(i)
            val _lunch = item.getString("DDISH_NM").replace("<br/>", "\n")
            val l = item.getString("MMEAL_SC_CODE")
            get_date = item.getString("MLSV_YMD")
            if (l.equals("1")){
                breakfast = _lunch
                Log.i("BREAK", breakfast)
            } else if (l.equals("2")){
                lunch = _lunch
            } else if (l.equals("3")) {
                dinner = _lunch
            } else {
                Log.i("??", item.toString())
            }
        }
        Log.i("!", "End")
        time = ""
        for (i in 0..7){
            time += get_date[i]
            if (i == 3 || i == 5) time += "-"
        }
        return arrayOf(breakfast, lunch, dinner, time)

    } catch (e: Exception) {
        Log.i("EE!","End")
        try {
            time = ""
            for (i in 0..7){
                time += date[i]
                if (i == 3 || i == 5) time += "-"
            }
        } catch (e: Exception) {
            get_date = LocalDateTime.now().format(format2)
            time = get_date.toString()
        }


        return arrayOf(no_data, no_data, no_data, time)
    }


}

@Composable
fun mainMenu(breakfast: String, lunch: String, dinner:String, loadedDay: String, loadLunch: (String) -> Unit) {
    ConstraintLayout (
        Modifier
            .fillMaxSize(),
    ) {
        val (titlec,hd, subtitle, lunchCompose, time, bottomc) = createRefs()
        title(modifier = Modifier
            .constrainAs(titlec){
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp, 5.dp)
                .constrainAs(hd) {
                    top.linkTo(titlec.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            color = Color.LightGray
        )
        Text(
            text="Made by seon0313",
            modifier = Modifier
                .fillMaxWidth()
                .padding(35.dp, 1.dp, 30.dp, 50.dp)
                .constrainAs(subtitle) {
                    top.linkTo(hd.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            textAlign = TextAlign.Right,
            style = Typography.titleMedium,
            fontSize = 12.sp
        )
        Column(
            modifier = Modifier
                .constrainAs(lunchCompose){
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(subtitle.bottom)
                    bottom.linkTo(time.top)
                    width = Dimension.percent(1.0f)
                    height = Dimension.fillToConstraints
                }
                .verticalScroll(rememberScrollState())
        ) {
            Lunch(breakfast,lunch,dinner, modifier = Modifier)
        }

        Text(
            "${loadedDay}",
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .padding(5.dp, 2.dp, 0.dp, 0.dp)
                .constrainAs(time) {
                    bottom.linkTo(bottomc.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
        controllBar(breakfast, lunch, dinner, loadedDay, loadLunch, modifier = Modifier
            .imePadding()
            .constrainAs(bottomc) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
    }
}
fun getYesterday(date: String, day:Int = -1): String{
    var calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    val date = dateFormat.parse(date)
    calendar.time = date
    calendar.add(Calendar.DAY_OF_YEAR, day)
    var TimeToDate = calendar.time
    var formatter = SimpleDateFormat("yyyyMMdd")
    return formatter.format(TimeToDate)
}
@Composable
fun controllBar(breakfast: String, lunch: String, dinner:String, loadedDay: String, loadLunch: (String) -> Unit, modifier: Modifier) {
    var open by remember { mutableStateOf(false) }
    Row (
        modifier = modifier
            .padding(0.dp, 25.dp, 0.dp, 0.dp)
            .navigationBarsPadding()
    ) {

        TextButton(onClick = {
            loadLunch(getYesterday(loadedDay))
        }, modifier = Modifier.fillMaxWidth(0.33f),
            colors = ButtonColors(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "<")
        }

        TextButton(onClick = {
            open = true
        }, modifier = Modifier.fillMaxWidth(0.5f),
            colors = ButtonColors(MaterialTheme.colorScheme.secondaryContainer,MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondaryContainer,MaterialTheme.colorScheme.primary)) {
            Text(text = stringResource(R.string.allergy_information))
        }
        TextButton(onClick = {
            loadLunch(getYesterday(loadedDay,1))
        }, modifier = Modifier.fillMaxWidth(),
            colors = ButtonColors(MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.primaryContainer,MaterialTheme.colorScheme.primary)) {
            Text(text = ">")
        }
        if (open) Allergy({
            open=false
        })
    }
}

@Composable
fun CaptureBitmap( //https://m1nzi.tistory.com/15
    content: @Composable ()->Unit
) : () -> Bitmap {
    val context = LocalContext.current

    val composeView = remember { ComposeView(context) }

    fun captureBitmap(): Bitmap = composeView.drawToBitmap()

    AndroidView(
        factory = {
            composeView.apply {
                setContent {
                    content.invoke()
                }
            }
        }
    )
    /* 아래 코드는 onclick 밖 변수 선언
        val context = LocalContext.current
        val file_toast = stringResource(R.string.file_save)
        val copy = stringResource(R.string.copy)
    */



    /*
    * 밑 코드는 캡처버튼 onclick 함수의 코드 현재 캡처기능 삭제로 여기로 이동
    */

    // val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
    // val fileName = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
    // val path =  "$folder/$fileName.png"
    //
    // val bitmap = capture.invoke()
    // Log.i("EEEEEE",bitmap.toString())
    // val fos: FileOutputStream
    // try{
    //     fos = FileOutputStream(File(path))
    //     bitmap.compress(Bitmap.CompressFormat.PNG,100,fos)
    //     fos.close()
    //     Toast.makeText(context, file_toast, Toast.LENGTH_LONG).show()
    //
    //
    //
    //     val file = FileProvider.getUriForFile(context, "com.seon06.school_lunch_viewer.provider",File(path))
    //     val send = Intent().apply {
    //         action = Intent.ACTION_SEND
    //         putExtra(Intent.EXTRA_STREAM,file)
    //         addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    //         type = "image/*"
    //         data = file
    //     }
    //
    //     val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE)
    //             as ClipboardManager
    //     val clipData = ClipData.newUri(context.contentResolver, "Image", file)
    //     clipboard.setPrimaryClip(clipData)
    //
    //     val shareIntent = Intent.createChooser(send, null)
    //     startActivity(context,shareIntent, Bundle())
    // }catch (e: IOException) {
    //     e.printStackTrace()
    // }

    return ::captureBitmap
}


@Composable
fun title(modifier: Modifier = Modifier.fillMaxWidth()) {
    Text(
        text = stringResource(R.string.to_day_lunch),
        modifier = modifier
            .padding(0.dp,80.dp,0.dp,0.dp),
        textAlign = TextAlign.Center,
        style = Typography.titleLarge,
        fontSize = 50.sp
    )
}

@Composable
fun Lunch(breakfast:String, lunch: String, dinner: String, modifier: Modifier) {
    Column (
        modifier = modifier
            .padding(10.dp, 0.dp)
            .background(MaterialTheme.colorScheme.inversePrimary, AbsoluteRoundedCornerShape(10.dp)),
    ) {
        val titleModifier = Modifier
            .padding(10.dp, 10.dp,0.dp,5.dp)
        val messageModifier = Modifier
            .padding(20.dp, 5.dp, 0.dp, 5.dp)

        Text(
            text= stringResource(R.string.breakfast),
            modifier = titleModifier,
            style = Typography.labelMedium,
            fontSize = 20.sp,
        )

        Text(
            text="${breakfast}",
            modifier = messageModifier,
            softWrap = true
        )



        HorizontalDivider(color = Color.Black)
        Text(
            text= stringResource(R.string.lunch),
            modifier = titleModifier,
            style = Typography.labelMedium,
            fontSize = 20.sp,
        )

        Text(
            text="${lunch}",
            modifier = messageModifier,
            softWrap = true
        )

        HorizontalDivider(color = Color.Black)
        Text(
            text= stringResource(R.string.dinner),
            modifier = titleModifier,
            style = Typography.labelMedium,
            fontSize = 20.sp
        )

        Text(
            text="${dinner}",
            modifier = messageModifier
                .padding(0.dp, 5.dp, 0.dp, 5.dp),
            softWrap = true
        )
    }
}

@Composable
fun Allergy(onClick: () -> Unit) {
    Dialog(
        onDismissRequest = { onClick() },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
        )
        ){
        Card(
            shape = RoundedCornerShape(15.dp)
        ) {
            Column (
                modifier = Modifier
                    .width(450.dp)
                    .wrapContentHeight()
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(text = stringResource(R.string.allergy_information),
                    textAlign = TextAlign.Center,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .padding(0.dp, 15.dp, 0.dp, 5.dp))

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .padding(0.dp,5.dp),
                    color = Color.LightGray
                )

                Text(text = stringResource(R.string.allergy)
                    .replace("| ", "\n"),
                    modifier = Modifier
                        .padding(5.dp))
            }
        }
    }
}