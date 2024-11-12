package com.newproject.helper.spigot.sidebar.pager;

import com.newproject.helper.spigot.sidebar.Sidebar;

@FunctionalInterface
public interface PageConsumer<R> {

    void accept(int page, int maxPage, Sidebar<R> sidebar);
}
