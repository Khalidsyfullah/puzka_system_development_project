package com.akapps.puzka;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class FragmentMenu extends Fragment {
    Button notepad, drawing, diary, wallet, routine, pdf, notes, links;
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = getLayoutInflater().inflate(R.layout.fragment_menu, container ,false);
        notepad = view.findViewById(R.id.button23);
        drawing = view.findViewById(R.id.button24);
        diary = view.findViewById(R.id.button25);
        wallet = view.findViewById(R.id.button26);
        routine = view.findViewById(R.id.button27);
        pdf = view.findViewById(R.id.button28);
        notes = view.findViewById(R.id.button29);
        links = view.findViewById(R.id.button30);

        notepad.setOnClickListener(v -> ((Centerpage) requireActivity()).notepadPageOpen());

        drawing.setOnClickListener(v -> ((Centerpage) requireActivity()).drawingPage());

        diary.setOnClickListener(v -> ((Centerpage) requireActivity()).diaryPage());

        wallet.setOnClickListener(v -> ((Centerpage) requireActivity()).walletPage());

        routine.setOnClickListener(v -> ((Centerpage) requireActivity()).routinePage());

        pdf.setOnClickListener(v -> ((Centerpage) requireActivity()).imagePageCerter());

        links.setOnClickListener(v -> ((Centerpage) requireActivity()).startLinks());

        notes.setOnClickListener(v -> ((Centerpage) requireActivity()).startNotes());

        return view;
    }
}
