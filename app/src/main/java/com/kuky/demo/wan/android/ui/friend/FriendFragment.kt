package com.kuky.demo.wan.android.ui.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.entity.FriendWebsite
import com.kuky.demo.wan.android.ui.websitedetail.WebsiteDetailFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description
 */
class FriendFragment : Fragment(), CoroutineScope by MainScope() {
    private val mViewModel by viewModel<FriendViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                FriendScreen(mViewModel, onNavToSite = {
                    WebsiteDetailFragment.viewDetail(
                        findNavController(),
                        R.id.action_mainFragment_to_websiteDetailFragment,
                        it.link
                    )
                })
            }

            launch { mViewModel.requestFriendWebsites() }
        }
    }
}

@Composable
fun FriendScreen(viewModel: FriendViewModel, onNavToSite: ((FriendWebsite) -> Unit)? = null) {
    val friendWebsites = viewModel.friendWebsiteList.collectAsState()

    Column(
        modifier = Modifier.border(
            BorderStroke(0.1f.dp, Color(0x77AAAAAA)),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
        ).background(Color.White)
    ) {
        Text(
            "常用网站(By Compose)",
            modifier = Modifier.fillMaxWidth()
                .border(
                    BorderStroke(0.1f.dp, Color(0x77AAAAAA)),
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ).padding(12.dp),
            color = Color(0xFFD81B60),
            textAlign = TextAlign.Center
        )

        LazyColumn(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 8.dp)) {
            items(friendWebsites.value, key = { it.id }) { website ->
                FriendWebsiteView(website, onNavToSite)
            }
        }
    }
}

@Composable
fun FriendWebsiteView(website: FriendWebsite, onNavToSite: ((FriendWebsite) -> Unit)?) {
    Card(modifier = Modifier.fillMaxWidth()
        .padding(1.dp)
        .clip(RoundedCornerShape(8.dp))
        .clickable { onNavToSite?.invoke(website) }
        .padding(vertical = 4.dp), elevation = 2.dp)
    {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        {
            Text(
                website.name, fontSize = 18.sp, color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp, top = 12.dp),
                fontWeight = FontWeight.Bold
            )

            Text(
                website.link, fontSize = 14.sp, color = Color.Gray,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}