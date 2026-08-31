package com.kang.kangapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kang.kangapp.data.LaundryRepository
import com.kang.kangapp.model.LaundryMachine
import com.kang.kangapp.model.LaundryStatus
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KangAppRoot() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF171A21),
                drawerContentColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 28.dp)
                ) {
                    Text(
                        text = "KangApp",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "TOOLS",
                        color = Color(0xFF7F8796),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    NavigationDrawerItem(
                        label = {
                            Text(
                                "Laundry 查询",
                                fontWeight = FontWeight.Bold
                            )
                        },
                        selected = true,
                        onClick = {
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFF2C3442),
                            selectedTextColor = Color.White,
                            unselectedContainerColor = Color.Transparent,
                            unselectedTextColor = Color(0xFFB9C0CC)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.weight(1f))

                    HorizontalDivider(color = Color(0xFF2A2F38))
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "KangApp v1.0",
                        color = Color(0xFF626A78),
                        fontSize = 11.sp
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "KangApp",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Text(
                                text = "☰",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            LaundryScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        }
    }
}

@Composable
private fun LaundryScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val statuses = remember {
        mutableStateMapOf<String, LaundryStatus>().apply {
            LaundryRepository.machines.forEach {
                put(it.name, LaundryStatus.WAITING)
            }
        }
    }

    var querying by remember { mutableStateOf(false) }
    var lastChecked by remember { mutableStateOf<String?>(null) }

    fun startQuery() {
        if (querying) return

        querying = true
        lastChecked = null

        LaundryRepository.machines.forEach {
            statuses[it.name] = LaundryStatus.QUERYING
        }

        scope.launch {
            try {
                val results = LaundryRepository.checkAll(context)

                results.forEach { result ->
                    statuses[result.machine.name] = result.status
                }

                lastChecked = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

            } catch (_: Exception) {
                LaundryRepository.machines.forEach {
                    statuses[it.name] = LaundryStatus.UNKNOWN
                }
            } finally {
                querying = false
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 18.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 24.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Laundry 状态查询",
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(7.dp))

            Text(
                text = "查看辅2地下 Laundry 设备当前是否可用。",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = ::startQuery,
                enabled = !querying,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF171A21),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF9CA3AF),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = if (querying) "查询中..." else if (lastChecked == null) "开始查询" else "重新查询",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = when {
                    querying -> "正在查询设备状态..."
                    lastChecked != null -> "最后查询：$lastChecked"
                    else -> "尚未查询"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(6.dp))
        }

        items(
            items = LaundryRepository.machines,
            key = { it.name }
        ) { machine ->
            MachineStatusCard(
                machine = machine,
                status = statuses[machine.name] ?: LaundryStatus.UNKNOWN
            )
        }
    }
}

@Composable
private fun MachineStatusCard(
    machine: LaundryMachine,
    status: LaundryStatus
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = machine.name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = machine.type,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            StatusBadge(status)
        }
    }
}

@Composable
private fun StatusBadge(status: LaundryStatus) {
    val (background, foreground) = when (status) {
        LaundryStatus.AVAILABLE ->
            Color(0xFFE9F7EF) to Color(0xFF197A43)

        LaundryStatus.BUSY ->
            Color(0xFFFDECEC) to Color(0xFFC62828)

        LaundryStatus.UNAVAILABLE ->
            Color(0xFFFFF3E0) to Color(0xFFB85C00)

        LaundryStatus.QUERYING ->
            Color(0xFFEAF2FF) to Color(0xFF2563EB)

        LaundryStatus.WAITING,
        LaundryStatus.UNKNOWN ->
            Color(0xFFF1F3F5) to Color(0xFF666666)
    }

    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(10.dp))
            .padding(horizontal = 13.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.label,
            color = foreground,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}
