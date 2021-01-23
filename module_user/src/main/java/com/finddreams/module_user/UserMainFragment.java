package com.finddreams.module_user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.finddreams.module_base.base.BaseFragment;
import com.finddreams.module_base.utils.RouteUtils;
import com.finddreams.module_base.utils.broadcast.BroadcastManager;
import com.finddreams.module_base.utils.broadcast.wrappers.ObserverWrapper;

import androidx.annotation.Nullable;

/**
 * Created by lx on 17-10-24.
 */
@Route(path = RouteUtils.User_Fragment_Main)
public class UserMainFragment extends BaseFragment {

    private TextView tv_login_state;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_fragment_main, null);
        initView(rootView);

        BroadcastManager.getInstance().getChannel("event").observe(this, new ObserverWrapper<Object>() {
            @Override
            public void onChanged(@Nullable Object o) {
                if (o instanceof Intent) {
                    tv_login_state.setText(((Intent) o).getAction());
                }
            }
        });
        return rootView;
    }

    private void initView(View rootView) {
        Button bt_login_state = rootView.findViewById(R.id.bt_login);
        tv_login_state = rootView.findViewById(R.id.tv_login_state);
        bt_login_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouteUtils.startLoginActivity();
            }
        });
    }
}
