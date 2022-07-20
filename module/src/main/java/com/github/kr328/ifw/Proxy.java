package com.github.kr328.ifw;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.RemoteException;

import com.github.kr328.magic.aidl.ServerProxy;
import com.github.kr328.magic.aidl.ServerProxyFactory;
import com.github.kr328.magic.aidl.TransactProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Proxy extends IPackageManager.Stub {
    public static final ServerProxyFactory<IPackageManager, Proxy> FACTORY;

    static {
        try {
            FACTORY = ServerProxy.createFactory(IPackageManager.class, Proxy.class, false);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private final IPackageManager original;

    public Proxy(IPackageManager original) {
        this.original = original;
    }

    @SuppressLint("PrivateApi")
    private Context getContext() {
        Context context = null;
        try {
            Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
            Method method = ActivityThread.getMethod("currentActivityThread");
            Object currentActivityThread = method.invoke(ActivityThread);//获取currentActivityThread 对象

            Method method2 = currentActivityThread.getClass().getMethod("getApplication");
            context = (Context) method2.invoke(currentActivityThread);//获取 Context对象
        } catch (Exception e) {
            e.printStackTrace();
        }
        return context;
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivities(
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivities(
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        for (String pkg : getContext().getPackageManager().getPackagesForUid(Binder.getCallingUid())) {
            if (pkg.equals("com.jakting.shareclean") || pkg.equals("com.jakting.shareclean.debug")) {
                if (!(intent.getAction().equals(Intent.ACTION_PROCESS_TEXT)
                        && intent.getType().equals("text/tigerinthewall")
                )){
                    return result;
                }else{
                    return new ParceledListSlice<>(new ArrayList<>());
                }
            }
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.ACTIVITY,
                        intent,
                        resolvedType
                )
        );
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivityOptions(
            ComponentName caller,
            Intent[] specifics,
            String[] specificTypes,
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentActivityOptions(
                caller,
                specifics,
                specificTypes,
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.ACTIVITY,
                        intent,
                        resolvedType
                )
        );
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentServices(
            Intent intent,
            String resolvedType,
            int flags,
            int userId
    ) throws RemoteException {
        final ParceledListSlice<ResolveInfo> result = original.queryIntentServices(
                intent,
                resolvedType,
                flags,
                userId
        );

        if (Firewall.get() == null) {
            return result;
        }

        return new ParceledListSlice<>(
                Firewall.get().filterResult(
                        result.getList(),
                        Firewall.IntentFirewall.FilterType.SERVICE,
                        intent,
                        resolvedType
                )
        );
    }
}
