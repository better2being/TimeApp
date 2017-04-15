package com.tsunami.timeapp.injector.component;

import com.tsunami.timeapp.injector.Fragment;
import com.tsunami.timeapp.injector.module.FragmentModule;
import com.tsunami.timeapp.ui.fragments.SettingFragment;

import dagger.Component;

/**
 * @author shenxiaoming
 */
@Fragment
@Component(dependencies = {ActivityComponent.class}, modules = {FragmentModule.class})
public interface FragmentComponent {
    void inject(SettingFragment fragment);
}
