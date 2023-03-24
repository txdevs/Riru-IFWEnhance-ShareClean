package com.github.kr328.ifw;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.os.Binder;
import android.os.Process;
import android.os.RemoteException;

import com.github.kr328.magic.aidl.ServerProxy;
import com.github.kr328.magic.aidl.ServerProxyFactory;
import com.github.kr328.magic.aidl.TransactProxy;

import java.util.ArrayList;

public class Proxy extends IPackageManager.Stub {
    public static final ServerProxyFactory<IPackageManager, Proxy> FACTORY =
            ServerProxy.mustCreateFactory(IPackageManager.class, Proxy.class, false);

    private final IPackageManager original;

    public Proxy(final IPackageManager original) {
        this.original = original;
    }

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentActivities(
            final Intent intent,
            final String resolvedType,
            final int flags,
            final int userId
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

        int callingUid = Binder.getCallingUid();
        if (callingUid == Process.ROOT_UID)
            return result;
        if (callingUid != Process.SYSTEM_UID) {
            String[] callingPackages = original.getPackagesForUid(callingUid);
            if (callingPackages != null)
                for (String pkg : callingPackages) {
                    if (pkg.equals("com.jakting.shareclean") || pkg.equals("com.jakting.shareclean.debug")) {
                        if (intent.getAction().equals(Intent.ACTION_PROCESS_TEXT)
                                && intent.getType().equals("text/tigerinthewall")) {
                            return new ParceledListSlice<>(new ArrayList<>());
                        }
                        return result;
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
    public ParceledListSlice<ResolveInfo> queryIntentActivities(
            final Intent intent,
            final String resolvedType,
            final long flags,
            final int userId
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

        int callingUid = Binder.getCallingUid();
        if (callingUid == Process.ROOT_UID)
            return result;
        if (callingUid != Process.SYSTEM_UID) {
            String[] callingPackages = original.getPackagesForUid(callingUid);
            if (callingPackages != null)
                for (String pkg : callingPackages) {
                    if (pkg.equals("com.jakting.shareclean") || pkg.equals("com.jakting.shareclean.debug")) {
                        if (intent.getAction().equals(Intent.ACTION_PROCESS_TEXT)
                                && intent.getType().equals("text/tigerinthewall")) {
                            return new ParceledListSlice<>(new ArrayList<>());
                        }
                        return result;
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
            final ComponentName caller,
            final Intent[] specifics,
            final String[] specificTypes,
            final Intent intent,
            final String resolvedType,
            final int flags,
            final int userId
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
    public ParceledListSlice<ResolveInfo> queryIntentActivityOptions(
            final ComponentName caller,
            final Intent[] specifics,
            final String[] specificTypes,
            final Intent intent,
            final String resolvedType,
            final long flags,
            final int userId
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
            final Intent intent,
            final String resolvedType,
            final int flags,
            final int userId
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

    @Override
    @TransactProxy
    public ParceledListSlice<ResolveInfo> queryIntentServices(
            final Intent intent,
            final String resolvedType,
            final long flags,
            final int userId
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

    @Override
    @TransactProxy
    public String[] getPackagesForUid(int uid) throws RemoteException {
        return original.getPackagesForUid(uid);
    }
}
