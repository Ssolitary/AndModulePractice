package com.finddreams.shoppingcart;

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
import com.finddreams.module_base.utils.eventbus.factory.BusFactory;
import com.finddreams.module_base.utils.eventbus.wrapper.ObserverWrapper;

import androidx.annotation.Nullable;

/**
 * Created by lx on 17-10-24.
 */
@Route(path = RouteUtils.ShoppingCart_Fragment_Main)
public class ShoppingCartMainFragment extends BaseFragment {
    TextView tv_loginstate;
    TextView tvGoodname;
    Button btGotoGooddetail;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shoppingcart_fragment_main, null);
        initView(rootView);

        BusFactory.ready().create("event").observe(this, new ObserverWrapper<Object>() {
            @Override
            public void onChanged(@Nullable Object o) {
                if (o instanceof Intent) {
                    tv_loginstate.setText(((Intent) o).getAction());
                }
            }
        });
        return rootView;
    }

    private void initView(View rootView) {
        tvGoodname = rootView.findViewById(R.id.tv_goodname);
        tv_loginstate = rootView.findViewById(R.id.tv_loginstate);
        btGotoGooddetail = rootView.findViewById(R.id.bt_goto_gooddetail);
        final String goodName = tvGoodname.getText().toString();
        btGotoGooddetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RouteUtils.startGoodDetailActivity(goodName);
            }
        });

    }
}
