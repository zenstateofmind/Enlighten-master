package com.example.nikhiljoshi.enlighten.ui.Fragment;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nikhiljoshi.enlighten.R;
import com.example.nikhiljoshi.enlighten.adapter.FriendAndPackAdapter;
import com.example.nikhiljoshi.enlighten.data.EnlightenContract;
import com.example.nikhiljoshi.enlighten.pojo.Friend;
import com.example.nikhiljoshi.enlighten.ui.Activity.LoginActivity;
import com.example.nikhiljoshi.enlighten.ui.Activity.MainActivity;
import com.example.nikhiljoshi.enlighten.ui.Activity.SelectFriendsActivity;
import com.twitter.sdk.android.Twitter;

import java.util.ArrayList;
import java.util.List;

import static com.example.nikhiljoshi.enlighten.ui.Fragment.SelectFriendsFragment.*;

/**
 * Created by nikhiljoshi on 6/7/16.
 */
public class ChosenFriendsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ChosenFriendsFragment.class.getSimpleName();
    public static final String PACK_ID_TAG = "pack_id_tag";
    private static final int LOAD_FRIENDS_LOADER = 0;

    private RecyclerView mRecyclerView;
    private FriendAndPackAdapter mFriendAndPackAdapter;
    private Long packId;
    private ActivityToStartOnFriendSelection activityToStartOnFriendSelectionEnum;
    private FriendSource friendsSourceEnum;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        final Bundle arguments = getArguments();
        activityToStartOnFriendSelectionEnum =
                (ActivityToStartOnFriendSelection) arguments.getSerializable(ACTIVITY_TO_START_ON_FRIENDS_SELECTION_TAG);

        friendsSourceEnum = (FriendSource) arguments.getSerializable(FRIEND_SOURCE_FOR_ADDING_NEW_FRIENDS_TAG);
        packId = arguments.getLong(PACK_ID_TAG);

        View rootView = inflater.inflate(R.layout.fragment_chosen_friends, container, false);
        mFriendAndPackAdapter = new FriendAndPackAdapter(getActivity());
        // Friends are being loaded through the cursor loader
        mFriendAndPackAdapter.loadPacksFromDb(packId);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.chosen_friends_recyclerView);
        mRecyclerView.setAdapter(mFriendAndPackAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOAD_FRIENDS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_pack_friends_menu, menu);
        if (packId != -1) {
            final MenuItem addFriendsOption = menu.findItem(R.id.add_friends);
            addFriendsOption.setTitle(R.string.move_friends_to_pack);

            final MenuItem addPackItem = menu.findItem(R.id.add_pack);
            addPackItem.setVisible(false);
            addPackItem.setEnabled(false);

            final MenuItem deletePack = menu.findItem(R.id.delete_pack);
            deletePack.setVisible(true);
            deletePack.setEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: If packId = -1, show add_friends, else show 'Move Friends To This Pack'
        switch (item.getItemId()) {
            case R.id.add_pack: {
                final Dialog addPackDialog = createAddPackDialog();
                addPackDialog.show();
                return true;
            } case R.id.add_friends: {
                Intent intent = new Intent(getContext(), SelectFriendsActivity.class);
                intent.putExtra(ACTIVITY_TO_START_ON_FRIENDS_SELECTION_TAG, activityToStartOnFriendSelectionEnum);
                intent.putExtra(FRIEND_SOURCE_FOR_ADDING_NEW_FRIENDS_TAG, friendsSourceEnum);
                intent.putExtra(PACK_ID_TAG, packId);
                getActivity().finish();
                getActivity().startActivity(intent);
                return true;
            } case R.id.delete_pack: {
                // ask if you want to delete for sure
                // if so, first delete the pack with this pack id
                //              delete the friends with this pack id
                //                  go back to MainActivity
                deletePack();
            }case R.id.logout: {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(getString(R.string.verify_user_wants_to_logout))
                        .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Twitter.getSessionManager().clearActiveSession();
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                builder.show();

            } default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void deletePack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.are_you_sure_about_deleting_packs))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteInfoFromDb();
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.show();

    }

    private void deleteInfoFromDb() {
        final long userId = Twitter.getSessionManager().getActiveSession().getUserId();
        final Uri packUri = EnlightenContract.PackEntry.buildPackUriWithPackId(userId, packId);
        final Uri friendUri = EnlightenContract.FriendEntry.buildUriWithCurrentUserIdAndPackId(userId, packId);

        final ContentResolver contentResolver = getActivity().getContentResolver();
        final int numRowsPackDeleted = contentResolver.delete(packUri, null, null);
        final int numRowsFriendsDeleted = contentResolver.delete(friendUri, null, null);

        Log.i(LOG_TAG, "Number of rows in pack deleted: " + numRowsPackDeleted + " " +
                        " and number of rows in friends table deleted: " + numRowsFriendsDeleted);
    }

    /**
     * Create the dialog box that allows users to add new packs
     */
    private Dialog createAddPackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.add_pack_dialog, null);
        final EditText packNameEditText =
                (EditText) dialogView.findViewById(R.id.pack_name);
        final EditText packDescrEditText =
                (EditText) dialogView.findViewById(R.id.pack_description);

        builder.setView(dialogView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String packName = packNameEditText.getText().toString();
                        final String packDescr = packDescrEditText.getText().toString();
                        if (packName == null || packName.trim().isEmpty() ||
                                packDescr == null || packDescr.trim().isEmpty()) {
                            Toast.makeText(getContext(), getString(R.string.fields_cannot_be_empty), Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.created_a_pack), Toast.LENGTH_LONG).show();
                            final ContentValues packData = EnlightenContract.PackEntry.getContentValues(packName, packDescr, packId /*parent pack id */);
                            final Uri uri = getContext().getContentResolver().insert(EnlightenContract.PackEntry.CONTENT_URI, packData);
                            final long packInsertedRowId = ContentUris.parseId(uri);
                            if (packInsertedRowId == -1) {
                                Log.e(LOG_TAG, "Problem inserting data into the pack table");
                            }
                            mFriendAndPackAdapter.loadPacksFromDb(packId);

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        List<Friend> friends = new ArrayList<>();
        long currentSessionUserId = Twitter.getSessionManager().getActiveSession().getUserId();
        Uri uriWithCurrentUserId = EnlightenContract.FriendEntry.buildUriWithCurrentUserIdAndPackId(currentSessionUserId, packId);

        return new CursorLoader(getActivity(),
                uriWithCurrentUserId,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFriendAndPackAdapter.loadFriendsFromDb(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
