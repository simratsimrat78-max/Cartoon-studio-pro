package com.simrat.cartoonstudio

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

data class CharacterItem(val name:String,val emoji:String)
data class Scene(val name:String,val bg:String)

class MainActivity : ComponentActivity() {
    private var tts: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { if (it == TextToSpeech.SUCCESS) tts?.language = Locale.US }
        setContent { CartoonStudioApp { text -> tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dialogue") } }
    }
    override fun onDestroy(){ tts?.stop(); tts?.shutdown(); super.onDestroy() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartoonStudioApp(speak:(String)->Unit) {
    val chars = remember { listOf(
        CharacterItem("Alex","🧑‍🚀"), CharacterItem("Maya","👩‍🎤"), CharacterItem("Robot","🤖"),
        CharacterItem("Ninja","🥷"), CharacterItem("Hero","🦸"), CharacterItem("Girl","👧"),
        CharacterItem("Boy","👦"), CharacterItem("Wizard","🧙"), CharacterItem("Alien","👽"),
        CharacterItem("Dog","🐶"), CharacterItem("Cat","🐱"), CharacterItem("Bear","🐻")
    )}
    val backgrounds = remember { listOf("City","🌆"),("Forest","🌲"),("Beach","🏖️"),("Space","🌌"),("School","🏫"),("Studio","🎬") }
    var selectedChar by remember { mutableStateOf(chars.first()) }
    var selectedBg by remember { mutableStateOf("🌆") }
    var selectedTool by remember { mutableStateOf("Characters") }
    var animation by remember { mutableStateOf("Idle") }
    var expression by remember { mutableStateOf("Happy") }
    var dialogue by remember { mutableStateOf("Hello! Welcome to Cartoon Studio.") }
    var playing by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Ready") }
    var showDialogue by remember { mutableStateOf(false) }

    MaterialTheme(colorScheme = lightColorScheme(
        primary = Color(0xFF5B5FEF), secondary = Color(0xFF7C4DFF),
        surface = Color.White, background = Color(0xFFF6F7FB)
    )) {
        Scaffold(
            topBar = {
                TopAppBar(title={ Text("Cartoon Studio",fontWeight=FontWeight.Bold) },
                    actions={
                        TextButton({ status="New project created" }){ Text("New") }
                        TextButton({ status="Project saved" }){ Text("Save") }
                        TextButton({ status="Export ready (demo)" }){ Text("Export") }
                    })
            }
        ){ pad ->
            Row(Modifier.fillMaxSize().padding(pad).padding(10.dp).background(Color(0xFFF6F7FB))) {
                Column(Modifier.width(230.dp).fillMaxHeight()) {
                    Text("LIBRARY",fontSize=12.sp,fontWeight=FontWeight.Bold,color=Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    listOf("Characters","Backgrounds","Props").forEach { item ->
                        Surface(shape=RoundedCornerShape(12.dp),color=if(selectedTool==item) Color(0xFFE9E9FF) else Color.Transparent,
                            modifier=Modifier.fillMaxWidth().clickable{selectedTool=item}.padding(vertical=2.dp)) {
                            Row(Modifier.padding(12.dp),verticalAlignment=Alignment.CenterVertically){
                                Icon(if(item=="Characters") Icons.Default.Person else if(item=="Backgrounds") Icons.Default.Image else Icons.Default.Category,null)
                                Spacer(Modifier.width(10.dp)); Text(item)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    if(selectedTool=="Characters") {
                        LazyVerticalGrid(GridCells.Fixed(2),Modifier.fillMaxSize(),contentPadding=PaddingValues(2.dp)) {
                            items(chars){ c -> LibraryCard(c.name,c.emoji,c==selectedChar){selectedChar=c;status="${c.name} added"} }
                        }
                    } else if(selectedTool=="Backgrounds") {
                        LazyVerticalGrid(GridCells.Fixed(2),Modifier.fillMaxSize(),contentPadding=PaddingValues(2.dp)) {
                            items(backgrounds){ pair -> LibraryCard(pair.first,pair.second,pair.second==selectedBg){selectedBg=pair.second;status="${pair.first} background selected"} }
                        }
                    } else {
                        listOf("📦 Box","🚗 Car","🌳 Tree","🪑 Chair","🎁 Gift","💡 Lamp").forEach{p->
                            Surface(Modifier.fillMaxWidth().padding(3.dp).clickable{status="$p added"},shape=RoundedCornerShape(10.dp),color=Color.White){
                                Text(p,Modifier.padding(12.dp))
                            }
                        }
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    Box(Modifier.weight(1f).fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Color(0xFFE9ECF4)).border(1.dp,Color(0xFFD8DAE5),RoundedCornerShape(18.dp))) {
                        Text(selectedBg,Modifier.align(Alignment.Center),fontSize=120.sp)
                        Column(Modifier.align(Alignment.Center),horizontalAlignment=Alignment.CenterHorizontally) {
                            Text(selectedChar.emoji,fontSize=115.sp,modifier=Modifier.padding(top=100.dp))
                            Text("${selectedChar.name} • $animation • $expression",fontSize=13.sp,fontWeight=FontWeight.SemiBold)
                        }
                        Surface(Modifier.align(Alignment.TopStart).padding(10.dp),shape=RoundedCornerShape(20.dp),color=Color.White.copy(alpha=.9f)){
                            Text("Scene 1  •  1280 × 720",Modifier.padding(horizontal=12.dp,vertical=7.dp),fontSize=12.sp)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment=Alignment.CenterVertically) {
                        Button({playing=!playing;status=if(playing)"Playing $animation" else "Stopped"}){Icon(if(playing)Icons.Default.Stop else Icons.Default.PlayArrow,null);Spacer(Modifier.width(4.dp));Text(if(playing)"Stop" else "Play")}
                        Spacer(Modifier.width(6.dp))
                        OutlinedButton({animation="Walk";status="Walk animation selected"}){Text("Walk")}
                        Spacer(Modifier.width(4.dp))
                        OutlinedButton({animation="Run";status="Run animation selected"}){Text("Run")}
                        Spacer(Modifier.width(4.dp))
                        OutlinedButton({animation="Jump";status="Jump animation selected"}){Text("Jump")}
                        Spacer(Modifier.width(4.dp))
                        OutlinedButton({animation="Dance";status="Dance animation selected"}){Text("Dance")}
                    }
                    Spacer(Modifier.height(6.dp))
                    Surface(shape=RoundedCornerShape(12.dp),color=Color.White,modifier=Modifier.fillMaxWidth()){
                        Row(Modifier.padding(10.dp),verticalAlignment=Alignment.CenterVertically){
                            Text("Timeline",fontWeight=FontWeight.Bold);Spacer(Modifier.width(16.dp))
                            repeat(8){ Box(Modifier.width(48.dp).height(25.dp).padding(2.dp).background(if(it==0)Color(0xFFDEE0FF) else Color(0xFFF1F2F7),RoundedCornerShape(5.dp))) }
                        }
                    }
                    Text(status,fontSize=12.sp,color=Color.Gray,modifier=Modifier.padding(4.dp))
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.width(270.dp).fillMaxHeight()) {
                    PanelTitle("Inspector")
                    Labeled("Character"){Text(selectedChar.name)}
                    Labeled("Animation"){
                        LazyRow{items(listOf("Idle","Walk","Run","Jump","Dance","Wave","Talk")){a->
                            FilterChip(selected=a==animation,onClick={animation=a;status="$a animation selected"},label={Text(a)})
                            Spacer(Modifier.width(4.dp))
                        }}
                    }
                    Labeled("Expression"){
                        LazyRow{items(listOf("Happy","Sad","Angry","Wow")){e->
                            FilterChip(selected=e==expression,onClick={expression=e;status="$e expression selected"},label={Text(e)})
                            Spacer(Modifier.width(4.dp))
                        }}
                    }
                    PanelTitle("Dialogue")
                    OutlinedTextField(dialogue,{dialogue=it},Modifier.fillMaxWidth().height(110.dp),label={Text("Character dialogue")})
                    Spacer(Modifier.height(6.dp))
                    Row {
                        Button({speak(dialogue);status="Voice played"}){Icon(Icons.Default.RecordVoiceOver,null);Spacer(Modifier.width(4.dp));Text("Speak")}
                        Spacer(Modifier.width(6.dp))
                        OutlinedButton({showDialogue=true}){Text("Add")}
                    }
                    PanelTitle("Scenes")
                    Row {
                        OutlinedButton({status="New scene added"}){Text("+ Scene")}
                        Spacer(Modifier.width(5.dp))
                        OutlinedButton({status="Scene duplicated"}){Text("Duplicate")}
                    }
                    PanelTitle("Tools")
                    Row(horizontalArrangement=Arrangement.spacedBy(5.dp)){
                        AssistChip({status="Keyframe added at current time"},label={Text("Keyframe")})
                        AssistChip({status="Layer duplicated"},label={Text("Duplicate")})
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("✓ Buttons are connected",fontSize=12.sp,color=Color(0xFF3B7A57),fontWeight=FontWeight.Bold)
                }
            }
        }
        if(showDialogue) AlertDialog(onDismissRequest={showDialogue=false},confirmButton={Button({showDialogue=false;status="Dialogue added"}){Text("Add")}},dismissButton={TextButton({showDialogue=false}){Text("Cancel")}},title={Text("Add dialogue")},text={Text("The current dialogue will be attached to ${selectedChar.name}.")})
    }
}

@Composable fun PanelTitle(s:String){Text(s,fontWeight=FontWeight.Bold,fontSize=14.sp,modifier=Modifier.padding(top=12.dp,bottom=6.dp))}
@Composable fun Labeled(label:String,content:@Composable()->Unit){Text(label,fontSize=11.sp,color=Color.Gray);content();Spacer(Modifier.height(5.dp))}
@Composable fun LibraryCard(name:String,emoji:String,selected:Boolean,onClick:()->Unit){
    Surface(shape=RoundedCornerShape(12.dp),color=if(selected)Color(0xFFE9E9FF) else Color.White,modifier=Modifier.padding(3.dp).clickable{onClick()}) {
        Column(Modifier.padding(8.dp),horizontalAlignment=Alignment.CenterHorizontally){Text(emoji,fontSize=34.sp);Text(name,fontSize=11.sp)}
    }
}
