package com.basecampers.basecamp.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.basecampers.basecamp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasecampTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                width = 1.dp,
                color = if (isError) PrimaryRed else BorderColor
            ),
            color = AppBackground
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = TextPrimary,
                    focusedContainerColor = AppBackground,
                    unfocusedContainerColor = AppBackground,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = visualTransformation,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon
            )
        }
    }
}

@Composable
fun BasecampButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryRed,
            disabledContainerColor = TextSecondary,
            contentColor = Color.White
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun BasecampOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SecondaryAqua),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextSecondary
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun BasecampCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            content()
        }
    }
}

@Composable
fun BasecampChip(
    text: String,
    modifier: Modifier = Modifier,
    type: ChipType = ChipType.INFO
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50.dp),
        color = when (type) {
            ChipType.CONFIRMED -> PrimaryRed
            ChipType.INFO -> SecondaryAqua
        }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

enum class ChipType {
    CONFIRMED, INFO
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasecampTopBar(
    title: String? = null,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            }
        },
        navigationIcon = {
            onBackClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

@Composable
fun BaseScreenContainer(
    content: @Composable () -> Unit,
    backgroundColor: Color = AppBackground,
    horizontalPadding: Dp = 24.dp,
    topPadding: Dp = 48.dp,
    bottomPadding: Dp = 24.dp
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPadding)
                .padding(top = topPadding, bottom = bottomPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content()
        }
    }
}

@Composable
fun BasecampDivider(
    text: String? = null,
    color: Color = BorderColor,
    thickness: Float = 1f
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = color,
            thickness = thickness.dp
        )
        text?.let {
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextSecondary
                )
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = color,
                thickness = thickness.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasecampTextFieldPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasecampTextField(
                value = "Normal Text Field",
                onValueChange = {},
                label = "Normal Text Field"
            )
            
            BasecampTextField(
                value = "Error State",
                onValueChange = {},
                label = "Error Text Field",
                isError = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasecampButtonPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasecampButton(
                text = "Normal Button",
                onClick = {}
            )
            
            BasecampButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false
            )
            
            BasecampButton(
                text = "Loading Button",
                onClick = {},
                isLoading = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasecampOutlinedButtonPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasecampOutlinedButton(
                text = "Outlined Button",
                onClick = {}
            )
            
            BasecampOutlinedButton(
                text = "Disabled Outlined",
                onClick = {},
                enabled = false
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasecampCardPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasecampCard(
                title = "Card Title",
                subtitle = "Card Subtitle",
                content = {
                    Text("Card content goes here")
                }
            )
            
            BasecampCard(
                content = {
                    Text("Card without title and subtitle")
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasecampChipPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasecampChip(
                text = "Confirmed",
                type = ChipType.CONFIRMED
            )
            
            BasecampChip(
                text = "Info",
                type = ChipType.INFO
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasecampTopBarPreview() {
    MaterialTheme {
        Column {
            BasecampTopBar(
                title = "Screen Title",
                onBackClick = {}
            )
            
            BasecampTopBar(
                title = "Screen Title"
            )
            
            BasecampTopBar()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BaseScreenContainerPreview() {
    MaterialTheme {
        BaseScreenContainer(
            content = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Screen Content")
                    BasecampButton(
                        text = "Example Button",
                        onClick = {}
                    )
                    BasecampTextField(
                        value = "",
                        onValueChange = {},
                        label = "Example Text Field"
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BasecampDividerPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasecampDivider()
            
            BasecampDivider(text = "OR")
            
            BasecampDivider(
                text = "Custom",
                color = PrimaryRed,
                thickness = 2f
            )
        }
    }
}

@Composable
fun VerticalCard(
    title: String,
    subtitle: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val TopBoxColor = Color(0xFFB6E4E6)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .width(180.dp)
            .wrapContentHeight()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(TopBoxColor),
                contentAlignment = Alignment.TopEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.Center),
                    tint = CardBackground
                )

                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, end = 12.dp) // ← move it in from edges
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "TAG",
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier
                            .background(PrimaryRed, shape = RoundedCornerShape(50))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onButtonClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = buttonText,
                        color = PrimaryRed
                    )
                }
            }
        }
    }
}

@Composable
fun Width(x0: Dp) {
    TODO("Not yet implemented")
}

@Preview(showBackground = true)
@Composable
fun VerticalCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 24.dp)
    ) {
        VerticalCard(
            title = "Report a problem",
            subtitle = "Need help?",
            description = "Let us know if something is not working right. We are here to help 24/7!",
            buttonText = "Report",
            onButtonClick = { /* Preview only */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VerticalCardBookRoomPreview() {
    VerticalCard(
        title = "Book a Room",
        subtitle = "Quick Booking",
        description = "Book a meeting room instantly. Check availability and reserve in seconds!",
        buttonText = "Book Now",
        onButtonClick = { /* Do nothing for preview */ },
        modifier = Modifier.padding(16.dp) // Add some padding for the preview
    )
}

@Composable
fun HorizontalOptionCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Payment,
    iconTint: Color = Color.White,
    iconBackground: Color = SecondaryAqua,
    textColor: Color = TextPrimary,
    textSize: Float = 16f,
    textWeight: FontWeight = FontWeight.Bold
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circular leading icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color = iconBackground, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Title
            Text(
                text = title,
                color = textColor,
                fontSize = textSize.sp,
                modifier = Modifier.weight(1f),
                fontWeight = textWeight
            )

            // Arrow
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go",
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HorizontalOptionCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Default preview
        HorizontalOptionCard(
            title = "Choose Title",
            onClick = { /* Do nothing for preview */ }
        )
        
        // Custom icon preview
        HorizontalOptionCard(
            title = "Help Center",
            onClick = { /* Do nothing for preview */ },
            icon = Icons.Default.Help,
            iconBackground = PrimaryRed
        )
        
        // Custom text styling preview
        HorizontalOptionCard(
            title = "Settings",
            onClick = { /* Do nothing for preview */ },
            icon = Icons.Default.Image,
            textColor = PrimaryRed,
            textSize = 18f
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasecampSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    hintColor: Color = TextSecondary,
    backgroundColor: Color = CardBackground
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(placeholder,
                color = hintColor,
                modifier = Modifier.padding(bottom = 1.dp)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = hintColor,
                modifier = Modifier.size(20.dp)
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = backgroundColor,
            cursorColor = TextPrimary,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(50),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var search by remember { mutableStateOf("") }

    BasecampSearchBar(
        query = search,
        onQueryChange = { search = it }
    )
}