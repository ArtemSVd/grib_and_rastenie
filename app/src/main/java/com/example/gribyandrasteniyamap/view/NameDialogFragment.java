package com.example.gribyandrasteniyamap.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.gribyandrasteniyamap.R;
import com.example.gribyandrasteniyamap.module.AdapterModule;
import com.example.gribyandrasteniyamap.module.DatabaseModule;
import com.example.gribyandrasteniyamap.module.ServiceModule;
import com.example.gribyandrasteniyamap.module.SharedPreferencesModule;
import com.example.gribyandrasteniyamap.module.UserModule;
import com.example.gribyandrasteniyamap.service.SharedPreferencesService;
import com.example.gribyandrasteniyamap.view.model.User;

import javax.inject.Inject;

import toothpick.Scope;
import toothpick.Toothpick;

import static com.example.gribyandrasteniyamap.service.SharedPreferencesService.USERNAME;
import static com.example.gribyandrasteniyamap.service.SharedPreferencesService.UUID;

public class NameDialogFragment extends AppCompatDialogFragment {

    @Inject
    SharedPreferencesService sharedPreferencesService;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Toothpick.inject(this, Toothpick.openScope("APP"));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater li = LayoutInflater.from(getActivity());
        View view = li.inflate(R.layout.name_dilaog, null);

        builder.setView(view)
                .setPositiveButton("ОК", (dialog, id) -> ok(view, dialog));
        return builder.create();
    }

    private void ok(View view, DialogInterface dialog) {
        EditText editText = view.findViewById(R.id.input_text);
        String name = editText.getText().toString();

        sharedPreferencesService.setValue(USERNAME, name);
        java.util.UUID uuid = java.util.UUID.randomUUID();
        sharedPreferencesService.setValue(UUID, java.util.UUID.randomUUID().toString());

        User user = User.builder()
                .name(name)
                .deviceName(uuid.toString())
                .build();

        Toothpick.reset();

        Scope appScope = Toothpick.openScope("APP");

        appScope.installModules(new SharedPreferencesModule(getContext()));

        appScope.installModules(
                new DatabaseModule(),
                new ServiceModule(),
                new AdapterModule(),
                new UserModule(user)
        );
        dialog.cancel();
    }
}
