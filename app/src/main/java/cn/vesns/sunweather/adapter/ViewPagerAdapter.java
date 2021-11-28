package cn.vesns.sunweather.adapter;


import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mList;



//    public ViewPagerAdapter(FragmentManager supportFragmentManager,Lifecycle lifecycle, List<Fragment> fragments) {
//        super(supportFragmentManager, lifecycle);
//        mList =fragments;
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return mList.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return super.getItemId(position);
//    }
//
//    @Override
//    public int getItemCount() {
//        return 0;
//    }


    public ViewPagerAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        super.destroyItem(container, position, object);
    }


}
