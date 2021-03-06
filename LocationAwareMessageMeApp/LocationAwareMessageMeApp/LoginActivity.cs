﻿using System;
using System.Collections.Generic;

using Android.App;
using Android.Content;
using Android.OS;
using Android.Widget;
using System.Net.Http;
using Newtonsoft.Json;
using LocationAwareMessageMeApp.Models;
using Android.Util;
using Android.Preferences;
using Android.Net;
using Android.Support.Design.Widget;

namespace LocationAwareMessageMeApp
{
    [Activity(Label = "LoginActivity")]
    public class LoginActivity : Activity
    {
        EditText etUserName;
        EditText etPassword;
        Button btnLogin;
        Button btnRegister;
        ISharedPreferences pref;
        ISharedPreferencesEditor prefEditor;

        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            SetContentView(Resource.Layout.Login);

            Init();
            // Create your application here
        }

        protected override void OnResume()
        {
            base.OnResume();
            //SystemRequirementsChecker.CheckWithDefaultDialogs(this);
        }

        private void Init()
        {
            etUserName = FindViewById<EditText>(Resource.Id.username);
            etPassword = FindViewById<EditText>(Resource.Id.password);
            btnLogin = FindViewById<Button>(Resource.Id.login);
            btnRegister = FindViewById<Button>(Resource.Id.signup);
            btnLogin.Click += OnLoginClicked;
            btnRegister.Click += OnRegisterClicked;
            pref = PreferenceManager.GetDefaultSharedPreferences(this);
            prefEditor = pref.Edit();
        }

        private void OnRegisterClicked(object sender, EventArgs e)
        {
            Intent registerUser = new Intent(this, typeof(RegisterActivity));
            StartActivity(registerUser);

        }

        private async void OnLoginClicked(object sender, EventArgs e)
        {
            string username = etUserName.Text;
            string password = etPassword.Text;

            if (!IsInValidInput())
            {
                //var connMgr = (ConnectivityManager)GetSystemService(ConnectivityService);
                var manager = (ConnectivityManager)GetSystemService(ConnectivityService);
                NetworkInfo info = manager.ActiveNetworkInfo;

                if (info != null)
                {
                    HttpClient client = new HttpClient();
                    Dictionary<string, string> parameters = new Dictionary<string, string>();

                    parameters.Add("grant_type", "password");
                    parameters.Add("username", username);
                    parameters.Add("password", password);

                    try
                    {
                        HttpResponseMessage result = await client.PostAsync(Constants.LOGIN_URL, new FormUrlEncodedContent(parameters));

                        if (result.IsSuccessStatusCode)
                        {
                            string jsonResult = await result.Content.ReadAsStringAsync();
                            // TokenResult is a custom model class for deserialization of the Token Endpoint
                            
                            var resultObject = JsonConvert.DeserializeObject<TokenModel>(jsonResult);
                            string token = Constants.BEARER + resultObject.Access_Token;

                            client.DefaultRequestHeaders.Add("Authorization", token);
                            var profile = await client.GetAsync(String.Format(Constants.USER_PROFILE_URL, resultObject.Access_Token));

                            var jsonProfile = await profile.Content.ReadAsStringAsync();
                            var user = JsonConvert.DeserializeObject<User>(jsonProfile);

                   
                            prefEditor.PutString(Constants.PREF_USER_TAG, JsonConvert.SerializeObject(user));
                            prefEditor.PutString(Constants.PREF_TOKEN_TAG, token);
                            prefEditor.Apply();

                            Intent openInbox = new Intent(this, typeof(InboxActivity));
                            StartActivity(openInbox);
                            Finish();
                        }
                        else
                        {
                            Log.Debug(Constants.TAG, "Error occured");


                            //todo: get message and show
                        }
                    }
                    catch (Exception oExcep)
                    {
                        Log.Error(Constants.TAG, oExcep.Message);
                    }

                }
                else
                {
                    Snackbar snackBar = Snackbar.Make((Button)sender, "No nternet Connection", Snackbar.LengthIndefinite);
                    //Show the snackbar
                    snackBar.Show();
                }

            }
        }

        private bool IsInValidInput()
        {
            bool isInValid = false;

            if (etUserName.Text == null)
            {
                etUserName.Error = "Username cannot be empty";
                isInValid = true;
            }
            if (etPassword.Text == null)
            {
                etPassword.Error = "Password cannot be empty";
                isInValid = true;
            }

            return isInValid;
        }
    }
}