package top.jowanxu.wanandroidclient.ui.activity

import Constant
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import toast
import top.jowanxu.wanandroidclient.R
import top.jowanxu.wanandroidclient.base.BaseActivity
import top.jowanxu.wanandroidclient.base.Preference
import top.jowanxu.wanandroidclient.ui.fragment.CommonUseFragment
import top.jowanxu.wanandroidclient.ui.fragment.HomeFragment
import top.jowanxu.wanandroidclient.ui.fragment.TypeFragment

/**
 * 主界面
 */
class MainActivity : BaseActivity() {
    private var lastTime: Long = 0
    private var currentIndex = 0
    private var homeFragment: HomeFragment? = null
    private var typeFragment: TypeFragment? = null
    private var commonUseFragment: CommonUseFragment? = null
    private val fragmentManager by lazy {
        supportFragmentManager
    }
    /**
     * check login for SharedPreferences
     */
    private var isLogin: Boolean by Preference(Constant.LOGIN_KEY, false)
    /**
     * local username
     */
    private var username: String by Preference(Constant.USERNAME_KEY, "")
    /**
     * local password
     */
    private var password: String by Preference(Constant.PASSWORD_KEY, "")

    /**
     *
     */
    private lateinit var navigationViewUsername: TextView

    /**
     *
     */
    private lateinit var navigationViewLogout: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar.run {
            title = getString(R.string.app_name)
            setSupportActionBar(this)
            setNavigationOnClickListener {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers()
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
        bottomNavigation.run {
            setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
            selectedItemId = R.id.navigation_home
        }
        drawerLayout.run {
            val toggle = ActionBarDrawerToggle(
                    this@MainActivity, this, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            addDrawerListener(toggle)
            toggle.syncState()
        }
        navigationView.run {
            setNavigationItemSelectedListener(onDrawerNavigationItemSelectedListener)
        }
        navigationViewUsername = navigationView.getHeaderView(0).findViewById<TextView>(R.id.navigationViewUsername)
        navigationViewLogout = navigationView.getHeaderView(0).findViewById<TextView>(R.id.navigationViewLogout)
        navigationViewUsername.run {
            if (!isLogin) {
                text = getString(R.string.not_login)
            } else {
                text = username
            }
        }
        navigationViewLogout.run {
            if (isLogin) {
                text = "点击登录"
            } else {
                text = getString(R.string.login_login)
            }
            setOnClickListener {
                toast("setOnClickListener")
                if (!isLogin) {
                    Intent(this@MainActivity, LoginActivity::class.java).run {
                        startActivityForResult(this, Constant.MAIN_REQUEST_CODE)
                    }
                } else {
                    isLogin = false
                    username = ""
                    password = ""
                    navigationViewUsername.text = getString(R.string.not_login)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // open search
        if (item.itemId == R.id.menuSearch) {
            Intent(this, SearchActivity::class.java).run {
                startActivity(this)
            }
            return true
        }
        when (item.itemId) {
            R.id.menuSearch -> {
                Intent(this, SearchActivity::class.java).run {
                    startActivity(this)
                }
                return true
            }
            R.id.menuHot -> {
                if (currentIndex == R.id.menuHot) {
                    commonUseFragment?.smoothScrollToPosition()
                }
                setFragment(R.id.menuHot)
                currentIndex = R.id.menuHot
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Dispatch incoming result to the correct fragment.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.MAIN_REQUEST_CODE && resultCode == RESULT_OK) {
            navigationViewUsername.text = data?.getStringExtra(Constant.CONTENT_TITLE_KEY)
            navigationViewLogout.text = getString(R.string.not_login)
        }
    }

    /**
     * 防止重叠
     */
    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        when (fragment) {
            is HomeFragment -> homeFragment ?: let { homeFragment = fragment }
            is TypeFragment -> typeFragment ?: let { typeFragment = fragment }
            is CommonUseFragment -> commonUseFragment ?: let { commonUseFragment = fragment }
        }
    }

    /**
     * 退出
     */
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime < 2 * 1000) {
            super.onBackPressed()
            finish()
        } else {
            toast(getString(R.string.double_click_exit))
            lastTime = currentTime
        }
    }

    /**
     * 显示对应Fragment
     */
    private fun setFragment(index: Int) {
        fragmentManager.beginTransaction().apply {
            homeFragment ?: let {
                HomeFragment().let {
                    homeFragment = it
                    add(R.id.content, it)
                }
            }
            typeFragment ?: let {
                TypeFragment().let {
                    typeFragment = it
                    add(R.id.content, it)
                }
            }
            commonUseFragment ?: let {
                CommonUseFragment().let {
                    commonUseFragment = it
                    add(R.id.content, it)
                }
            }
            hideFragment(this)
            when (index) {
                R.id.navigation_home -> {
                    toolbar.title = getString(R.string.app_name)
                    homeFragment?.let {
                        this.show(it)
                    }
                }
                R.id.navigation_type -> {
                    toolbar.title = getString(R.string.app_name)
                    typeFragment?.let {
                        this.show(it)
                    }
                }
                R.id.menuHot -> {
                    toolbar.title = getString(R.string.hot_title)
                    commonUseFragment?.let {
                        this.show(it)
                    }
                }
            }
        }.commit()
    }

    /**
     * 隐藏所有fragment
     */
    private fun hideFragment(transaction: FragmentTransaction) {
        homeFragment?.let {
            transaction.hide(it)
        }
        typeFragment?.let {
            transaction.hide(it)
        }
        commonUseFragment?.let {
            transaction.hide(it)
        }
    }

    /**
     * NavigationItemSelect监听
     */
    private val onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                setFragment(item.itemId)
                return@OnNavigationItemSelectedListener when (item.itemId) {
                    R.id.navigation_home -> {
                        if (currentIndex == R.id.navigation_home) {
                            homeFragment?.smoothScrollToPosition()
                        }
                        currentIndex = R.id.navigation_home
                        true
                    }
                    R.id.navigation_type -> {
                        if (currentIndex == R.id.navigation_type) {
                            typeFragment?.smoothScrollToPosition()
                        }
                        currentIndex = R.id.navigation_type
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

    private val onDrawerNavigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_like -> {
                if (!isLogin) {
                    Intent().run {
                        startActivityForResult(this, Constant.MAIN_REQUEST_CODE)
                    }
                    return@OnNavigationItemSelectedListener true
                    //
                }
            }
            R.id.nav_bookmark -> {
                if (!isLogin) {
                    Intent().run {
                        startActivityForResult(this, Constant.MAIN_REQUEST_CODE)
                    }
                    return@OnNavigationItemSelectedListener true
                    //
                }
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        true
    }
}