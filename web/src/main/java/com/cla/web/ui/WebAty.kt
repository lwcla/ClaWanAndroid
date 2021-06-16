package com.cla.web.ui

import android.view.KeyEvent
import android.widget.LinearLayout
import com.alibaba.android.arouter.facade.annotation.Route
import com.cla.wan.base.bean.WebParams
import com.cla.wan.base.config.BaseConfig
import com.cla.wan.base.config.WebPath
import com.cla.wan.base.ui.BaseAty
import com.cla.web.R
import com.just.agentweb.AgentWeb
import kotlinx.android.synthetic.main.activity_web.*

@Route(path = WebPath.WEB_ACTIVITY)
class WebAty : BaseAty() {

    private val preAgentWeb by lazy {

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )

        AgentWeb.with(this)
            .setAgentWebParent(llContent, params)
            .useDefaultIndicator()
            .createAgentWeb()
            .ready()
    }

    private val agentWeb by lazy { preAgentWeb.get() }

    private val webParams by lazy { intent?.getParcelableExtra(BaseConfig.WEB_PARAMS_KEY) as? WebParams? }

    override fun setup() {
        setContentView(R.layout.activity_web)
        titleBar.initBar(this, bgColorRes = R.color.white, centerTextRes = R.string.module_info)
        preAgentWeb.go(webParams?.url ?: "")
    }

    override fun onBackPressed() {

        if (agentWeb.back()) {
            return
        }

        super.onBackPressed()
    }

    override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (agentWeb.handleKeyEvent(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}