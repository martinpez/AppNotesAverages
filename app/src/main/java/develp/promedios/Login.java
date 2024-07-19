package develp.promedios;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {

    private ImageView imgbtn_google ,imgbtn_twitter ,imgbtn_facebook;
    private EditText password_edit , email_edit;
    private Button btn_register , btn_login;
    public String email , password;
    private static final String TAG = "RegisterActivity";

    // Constante para el código de solicitud
    private static final int RC_SIGN_IN = 7;

    // Variables para FirebaseAuth y GoogleSignInClient
    private FirebaseAuth auth;
   private GoogleSignInClient mGoogleSignInClient;

    private View rootView;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get a reference to the root view and not obtener la vista on null
        rootView = view;

            // Button
        btn_login =  getActivity().findViewById(R.id.btn_login);
        btn_register = getActivity().findViewById(R.id.btn_register);


        // Editext
        password_edit = getActivity().findViewById(R.id.password_edit);
        email_edit = getActivity().findViewById(R.id.email_edit);

        // imagesView
        imgbtn_google = getActivity().findViewById(R.id.imgbtn_google);

        // auth firebase
        auth = FirebaseAuth.getInstance();

        // Botton registrar accaunt
        btn_register.setOnClickListener(view13 -> {
            password = password_edit.getText().toString().trim();
            email = email_edit.getText().toString().trim();
            //System.out.println("XXD " + password);
            //System.out.println(email);
            createAccount(email ,password);
        });

        // Button Login
        btn_login.setOnClickListener(view12 -> {
            password = password_edit.getText().toString().trim();
            email = email_edit.getText().toString().trim();
            signIn(email , password);
            Navigation.findNavController(view12).navigate(R.id.mainApp);
        });


        // BUTTON AUTHENTICATION GOOGLE
        imgbtn_google.setOnClickListener(view1 -> signInGoogle());

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Crear un cliente de Google Sign-In con las opciones configuradas
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);



    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verificar si el resultado es de la solicitud de inicio de sesión de Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Inicio de sesión de Google fue exitoso, autenticar con Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    // Manejar el caso en que la cuenta sea nula
                    Log.w("TAG", "Google SignIn Account is null");
                }
            } catch (ApiException e) {
                // Error en el inicio de sesión de Google
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }


    // Autenticar en Firebase con la cuenta de Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        // Obtener las credenciales de autenticación de Google
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        // Iniciar sesión en Firebase con las credenciales de Google
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso, actualiza la interfaz de usuario con la información del usuario
                        Log.d("TAG", "signInWithCredential:success");
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // Si falla el inicio de sesión, muestra un mensaje al usuario.
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }




    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
           currentUser.reload();
        }
    }

    private void createAccount(String email, String password) {
       // System.out.println("create la contra " + password);
        //System.out.println("Create el email " + email);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(getContext(), "Authentication success.", Toast.LENGTH_SHORT).show();
                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    public void signIn(String email , String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = auth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // El usuario está registrado, puedes redirigirlo a otra actividad o actualizar la UI
            Toast.makeText(getContext(), getString(R.string.Login_enter), Toast.LENGTH_SHORT).show();
            if (rootView != null){
                Navigation.findNavController(rootView).navigate(R.id.mainApp);
            }

        } else {
            // El registro falló, permanece en la misma actividad
            Toast.makeText(getContext(), "Please try again.", Toast.LENGTH_SHORT).show();
        }

    }


}