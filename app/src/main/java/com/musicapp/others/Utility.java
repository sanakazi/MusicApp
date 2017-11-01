package com.musicapp.others;

/**
 * Created by PalseeTrivedi on 12/21/2016.
 */

public class Utility {

 //public static String baseUrl="http://192.168.168.5:9000/WebServices/AppServices.svc/";   //for testing
 public static String baseUrl="http://boxofficecapsule.com:91/WebServices/AppServices.svc/";
    public static final String BASE_URL_1="http://boxofficecapsule.com:91/WebServices/UserServices.svc/";
   public static String register=baseUrl+"Register";
    public static String login=baseUrl+"Login";
    public static String forgotPassword=baseUrl+"ForgotPassword";
    public static String verifyOtp=baseUrl+"VerifyOTP";
   // public static String search=baseUrl+"SearchScreenResult?";
   public static String search=baseUrl+"SearchScreenResult_V2?";
    public static final String home= baseUrl+"LandingScreen";
    public static final String home2= baseUrl+"LandingScreen_V2?";
  public static final String homelist_url = baseUrl+"GetSongsByCategoryId?categoryId=";
    public static final String homelist_url2 = baseUrl+"GetSongsByCategoryId_V2?categoryId=";
  public static final String home_details_url = baseUrl+"GetSongs?categoryId=";
    public static final String home_details_url2 = baseUrl+"GetSongs_V2?categoryId=";

    /*public static String home= baseUrl+"LandingScreen?UserId=1&DeviceId=556";*/
    public static String term= baseUrl+"getTermAndCondition";
    public static String resetPwd= baseUrl+"ResetPassword";
    public static String logout= baseUrl+"Logout";

    public static final String GET_ALL_GENRES=BASE_URL_1+"GetAllGenre?";
    public static final String ADD_TO_FAV_GENRES=BASE_URL_1+"ManageFavoriteGenre";
    public static final String GET_ALL_ARTISTS=BASE_URL_1+"GetAllArtist?";
    public static final String ADD_TO_FAV_Artists=BASE_URL_1+"ManageFavoriteArtist";
    public static final String CONTINUE_LOGIN=baseUrl+"ContinueToLogin?";
    public static final String GET_FAV_DATA= BASE_URL_1 + "GetFavData?";
    public static final String GET_USER_PROFILE_DATA = BASE_URL_1+"GetUserProfileData?";
    public static final String SUBSCRIPTION_URL = "http://49.50.67.37:91/ccavenue/buysubscription.aspx?";
    public static final String CHECK_SUBSCRIPTION_URL = BASE_URL_1+"CheckUserSubscription";
    public static final String BROWSE_ITEM_CLICK = baseUrl+"BrowseData?";
    public static final String LIVE_STREAM_URL = baseUrl+"ConcertData?";
    public static final String BROWSE_URL = baseUrl + "getCategoryListForBrowse?";

    public static String creatPlaylist = BASE_URL_1 + "CreateUserPlaylist";
    public static String renamePlaylist = BASE_URL_1 + "RenamePlaylist";
    public static String getplaylist = BASE_URL_1 + "GetPlaylist?";
    public static String songLike = BASE_URL_1 + "SongLike";
    public static String deleteplaylist = BASE_URL_1 + "deleteUserPlaylist";
    public static String getsongPlaylist = BASE_URL_1 + "GetSongsFromPlaylist?";
    public static String addSongtoPlaylist = BASE_URL_1 + "AddToUserPlaylist";
    public static String deletesongfromPlaylist = BASE_URL_1 + "RemoveSongsFromUserPlaylist";
    public static String newPassword=baseUrl+"NewPassword";

}
