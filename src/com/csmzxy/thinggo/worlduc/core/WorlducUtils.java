package com.csmzxy.thinggo.worlduc.core;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.csmzxy.thinggo.worlduc.WorlducCfg;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

public class WorlducUtils {
	private static String TAG = WorlducCfg.TAG;
	private static int TIME_OUT = 3000;
	private static Map<String, String> cookies;
	private static String myUid;
	private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0";
	private static FileCache fileCache;
	private static Context context;

	private WorlducUtils() {

	}

	private static boolean isNetConnected() {
		return isNetConnected(context);
	}

	public static boolean isNetConnected(Context ctx) {
		NetStatus s = checkNetStatus(ctx);
		return s == NetStatus.mobile || s == NetStatus.other;
	}

	public static NetStatus checkNetStatus() {
		return checkNetStatus(context);
	}

	public static NetStatus checkNetStatus(Context context) {
		if (context == null) {
			Log.i(TAG, " ----unknown -----");
			return NetStatus.unknown;
		}
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		if (mNetworkInfo == null) {
			Log.i(TAG, " ----none -----");
			return NetStatus.none;
		}
		if (mNetworkInfo.isAvailable()) {
			switch (mNetworkInfo.getType()) {
			case ConnectivityManager.TYPE_MOBILE:
			case ConnectivityManager.TYPE_MOBILE_DUN:
			case ConnectivityManager.TYPE_MOBILE_HIPRI:
			case ConnectivityManager.TYPE_MOBILE_MMS:
			case ConnectivityManager.TYPE_MOBILE_SUPL:
				Log.i(TAG, " ----mobile -----");
				return NetStatus.mobile;
			default:
				Log.i(TAG, " ----other -----");
				return NetStatus.other;
			}
		} else {
			Log.i(TAG, " ----disconnect -----");
			return NetStatus.disconnect;
		}
	}

	public static void createFileCache(Context ctx) {
		context = ctx;
		if (fileCache == null) {
			fileCache = new FileCache(context);
		}
		if(WorlducCfg.IsClearCache)
			fileCache.clear();
	}

	/**
	 * 登录世界大学城
	 * 
	 * @param userName
	 * @param password
	 * @return 成功返回true,失败返回false
	 */
	public static boolean login(String userName, String password) {
		try {
			Connection.Response res = Jsoup
					.connect("http://www.worlduc.com/index.aspx")
					.timeout(TIME_OUT*10)
					.userAgent(USER_AGENT)
					.timeout(TIME_OUT).data("op", "Login")
					.data("email", userName).data("pass", password)
					.method(Method.POST).execute();
			String data = res.body();
			int x1 = data.indexOf("userid");
            if (x1 >= 0)
            {
            	cookies = res.cookies();
                data = data.substring(x1 + 8);
                x1 = data.indexOf(",");
                myUid = data.substring(0,x1).trim();
                Jsoup.connect("http://worlduc.com/SpaceShow/Index.aspx?uid=134940")
                		.cookies(cookies).method(Method.GET).execute();                
            } 			
		} catch (Exception e) {
			Log.e(TAG, "---login---");
			e.printStackTrace();
		}
		return !TextUtils.isEmpty(myUid);
	}

	/**
	 * 给好友留言
	 * 
	 * @param message
	 *            留言内容
	 * @param uid
	 *            好友id
	 * @return true:留言成功,false:留言失败
	 */
	public static boolean sendLeaveWord(String message, String uid) {
		try {
			if (!isNetConnected())
				return false;
			String url = "http://www.worlduc.com/SpaceManage/Ajax/Ajax_LeaveWord.aspx";
			Connection connection = Jsoup.connect(url).cookies(cookies);
			Connection.Response res = connection.timeout(TIME_OUT)
					.data("op", "AddLeaveWord").data("receiver", uid)
					.data("Content", message).method(Method.POST).execute();
			// cookies = connection.response().cookies();
			String str = message.substring(0, message.length() > 5 ? 5
					: message.length());
			if (res.body().indexOf(str) >= 0) {
				return true;
			}
		} catch (Exception e) {
			Log.e(TAG, "---sendLeaveWord---\r\n" + e.getMessage());
		}
		return false;
	}

	/**
	 * 回复打招呼
	 * 
	 * @param fid
	 * @return
	 */
	public static boolean agreeGreet(String fid) {
		if (!isNetConnected())
			return false;
		String url = "http://www.worlduc.com/SpaceManage/Ajax/Ajax_Request.aspx";
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT);
			Connection.Response res = connection.data("op", "AgreeGreet")
					.data("fid", fid).method(Method.POST)
					.ignoreContentType(true).execute();
			Log.e(TAG, "---agreeGreet---" + res.body());
			if (res.body().equals("1")) {
				return true;
			}

		} catch (Exception e) {
			Log.e(TAG, "---agreeGreet---");
			e.printStackTrace();
		}
		return false;
	}

	public static LeaveWord replyLeaveWord(String message, String wordId) {
		/*
		 * t=留言ID,r=回复人ID
		 * http://www.worlduc.com/SpaceManage/Ajax/Ajax_LeaveWord.
		 * aspx?t=6686174&r=134940 Content <img
		 * src="http://www.worlduc.com/kindeditor401/plugins/emoticons/images/0.gif"
		 * alt="" border="0" />,好好学习！ op AddReply
		 */
		if (!isNetConnected())
			return null;
		try {
			String url = "http://www.worlduc.com/SpaceManage/Ajax/Ajax_LeaveWord.aspx";
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT);
			Connection.Response res = connection.data("t", wordId)
					.data("r", myUid).data("op", "AddReply")
					.data("Content", message).method(Method.POST)
					.ignoreContentType(true).execute();
			String str = message.substring(0, message.length() > 5 ? 5
					: message.length());
			LeaveWord replyWord = null;
			if (res.body().indexOf(str) >= 0) {
				Elements elReplys = res.parse()
						.select("div.leaveword_li_reply");
				if (elReplys != null) {
					for (Element r : elReplys) {
						wordId = r.id().substring(5);
						Element elAuthor = r.select(".headImage_2").get(0);
						String homeUrl = elAuthor.attr("href");
						String authorName = elAuthor.select("img").get(0)
								.attr("alt");
						String imgUrl = elAuthor.select("img").get(0)
								.attr("src");
						String time = r.select("span.time").text();
						Elements a = r.select("div.leaveword_li_reply_content");
						String wordText = a.html().replaceAll(
								"<a.*?href=(?:\"|')(.*?)(?:\"|').*?>.*?</a>",
								"");
						Person author = new Person(authorName, imgUrl, homeUrl,
								"");
						replyWord = new LeaveWord(wordId, wordText, time,
								author);
						// debugPrint(replyWord.toString());
					}
				}
				return replyWord;
			}
		} catch (Exception e) {
			Log.e(TAG, "---replyLeaveWord---");
			e.printStackTrace();
		}
		return null;
	}

	public static File GetImageFromUrl(String url, File outfile) {
		Log.i(TAG, "---GetImageFromUrl(" + url + ")---");
		if (!isNetConnected())
			return outfile;
		try {
			Response res = Jsoup.connect(url).referrer(url)
					.timeout(TIME_OUT * 10).cookies(cookies)
					.ignoreContentType(true).execute();
			FileOutputStream fos = new FileOutputStream(outfile);
			fos.write(res.bodyAsBytes());
			fos.close();
		} catch (Exception e) {
			Log.e(TAG, "---GetImageFromUrl(" + url + ")---\r\n");
			e.printStackTrace();
		}

		return outfile;
	}

	public static void getReplyWordListBy(String wordId, String authId,
			PagerModel<LeaveWord> pager) {

		String url = "http://www.worlduc.com/SpaceShow/leaveword/Reply.aspx?uid=%s&tid=%s";
		url = String.format(url, authId, wordId);
		List<LeaveWord> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		int n = getLeaveWordList(list, url);
	}

	public static void getReplyWordListBy(String url,
			PagerModel<LeaveWord> pager) {
		List<LeaveWord> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		int n = getLeaveWordList(list, url);
	}

	private static synchronized int getLeaveWordList(List<LeaveWord> list,
			String url) {
		list.clear();
		int pageCount = 0;
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			// cookies = connection.response().cookies();
			Element root = doc.getElementById("LeaveWordUL");
			if (root == null)
				return pageCount;
			Elements links = root.children();
			for (Element el : links) {
				/*
				 * <li class="leaveword_li list" id="MainDiv11542074"> <div
				 * id="leaveword11542074" class="leaveword_li_main"> <div
				 * class="leaveword_li_user fl"> <a target="_blank"
				 * href="/SpaceShow/Index.aspx?uid=134951" class="headImage_1">
				 * <img alt="陈翠娥"
				 * src="/uploadImage/head/x0/201312435011xg9wB.png"> </a> <span
				 * class="mt5"> <a href="/SpaceShow/Index.aspx?uid=134951"
				 * target="_blank">陈翠娥</a> </span> <span>(湖南)</span> </div> <div
				 * class="leaveword_li_content"> <span class="time">1小时前</span>
				 * <div
				 * class="ml10 mt10 overflow_word">亲，我们学校终于开学了。本学期本人开设了一门公选课
				 * 《世界大学城空间设计》，欢迎访问我的空间相关栏目，提出宝贵意见。</div> <div
				 * class="leaveword_li_menu ml10 mt10 clearfix w"> <a href=
				 * "/SpaceShow/leaveword/Reply.aspx?uid=134940&amp;tid=11542074"
				 * target="_blank" class="fl" id=
				 * "includeReplyNum/SpaceShow/leaveword/Reply.aspx?uid=134940&amp;tid=11542074"
				 * >查看全部回复(<span id=
				 * "replyNum/SpaceShow/leaveword/Reply.aspx?uid=134940&amp;tid=11542074"
				 * >1</span>)</a> <div class="fr"> <a href="javascript:void(0)"
				 * onclick="javascript:SendPrivateLetter(134951,'陈翠娥')"
				 * title="发私信">发私信</a> <a href="javascript:void(0)"
				 * onclick="javascript:SetReply(11542074,'134940')">回复</a> <a
				 * href="javascript:void(0)"
				 * onclick="javascript:DelLeaveWord(11542074,134951,134940)"
				 * >删除</a> </div> </div> </div> </div> <div
				 * class="leaveword_li_reply list_1 cb pl5 pt5 pb5"
				 * id="Reply11543685"> <div id="ReplyOption11543685"> <div
				 * class="fl"> <a target="_blank"
				 * href="/SpaceShow/Index.aspx?uid=134940" class="headImage_2">
				 * <img alt="吴名星"
				 * src="/uploadImage/head/x0/2012827132911SdXXb.png"> </a>
				 * </div> <div class="ml50 pr"> <div> <a target="_blank"
				 * href="/SpaceShow/Index.aspx?uid=134940">我</a> 的回复：<span
				 * class="time">41分钟前</span> </div> <div
				 * class="leaveword_li_reply_content mt5 overflow_word"> <img
				 * src=
				 * "http://www.worlduc.com/kindeditor401/plugins/emoticons/images/0.gif"
				 * border="0" alt="">，好的！<a href="javascript:void(0)"
				 * onclick="javascript:DelReplyOption(11543685,11542074,134940)"
				 * >删除</a> </div> <div class="cb"></div> </div> </div> </div>
				 * <div id="textDiv11542074"> <div
				 * class="leaveword_li_reply_txt reply_txt"
				 * id="defaultContent11542074"
				 * onclick="javascript:SetReply(11542074,'134940')">回复……</div>
				 * </div> </li>
				 */
				String wordId = el.id().substring(7);
				// 获取留言人信息
				Element elAuthor = el.getElementById("leaveword" + wordId)
						.select(".headImage_1").get(0);

				String homeUrl = elAuthor.attr("href");
				String authorName = elAuthor.select("img").get(0).attr("alt");
				String imgUrl = elAuthor.select("img").get(0).attr("src");
				String location = el.select("div.leaveword_li_user > span")
						.get(1).text();
				String time = el.select("div.leaveword_li_content > span")
						.text();
				String wordText = el.select(
						"div.leaveword_li_content > div.overflow_word").html();

				Person author = new Person(authorName, imgUrl, homeUrl,
						location);
				LeaveWord leaveWord = new LeaveWord(wordId, wordText, time,
						author);
				list.add(leaveWord);

				// debugPrint(leaveWord.toString());
				// 获取留言信息
				Elements elReplys = el.select("div.leaveword_li_reply");
				if (elReplys != null) {
					for (Element r : elReplys) {
						wordId = r.id().substring(5);
						elAuthor = r.select(".headImage_2").get(0);
						homeUrl = elAuthor.attr("href");
						authorName = elAuthor.select("img").get(0).attr("alt");
						imgUrl = elAuthor.select("img").get(0).attr("src");
						time = r.select("span.time").text();
						Elements a = r.select("div.leaveword_li_reply_content");
						wordText = a.html().replaceAll(
								"<a.*?href=(?:\"|')(.*?)(?:\"|').*?>.*?</a>",
								"");
						author = new Person(authorName, imgUrl, homeUrl, "");
						LeaveWord replyWord = new LeaveWord(wordId, wordText,
								time, author);
						leaveWord.addReplyWord(replyWord);
						// debugPrint(replyWord.toString());
					}
				}
			}
			Element epager = doc
					.getElementById("ctl00_ContentPlaceHolderMain_DivPage");
			if (epager != null) {
				String pageData = epager.text();
				int x = pageData.indexOf("共");
				if (x >= 0) {
					pageData = pageData.substring(x + 1);
					x = pageData.indexOf("页");
					pageData = pageData.substring(0, x);
					pageCount = Integer.parseInt(pageData);
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "---getLeaveWordList---" + url);
			e.printStackTrace();
		}
		return pageCount;
	}

	public static synchronized void getLeaveWordList(PagerModel<LeaveWord> pager) {
		List<LeaveWord> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		int iPage = pager.getCurPage();
		String url = "http://www.worlduc.com/SpaceManage/LeaveWord/LeaveWordList.aspx?Page="
				+ iPage;
		int pageCount = getLeaveWordList(list, url);
		pager.setPageCount(pageCount);

	}

	public static synchronized void getLeaveWordListByTopic(
			PagerModel<LeaveWord> pager, String tid) {
		List<LeaveWord> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		int iPage = pager.getCurPage();
		String url = "http://group.worlduc.com/GroupShow/TopicReply.aspx?tid=%s&Page=%s";
		url = String.format(url, tid, iPage);
		Log.i(TAG, "--getLeaveWordListByTopic" + url + "--");
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			// cookies = connection.response().cookies();
			Element root = doc.getElementById("divTopicReplyList");
			if (root == null)
				return;
			Elements links = root.children();
			for (Element el : links) {
				// liTopicReply4945783
				if (TextUtils.isEmpty(el.id()))
					break;
				// Log.i(TAG, "--"+el.outerHtml()+"--");
				String wordId = el.id().substring(12);
				Elements xx = el.select("a.head_u50");
				if (xx == null)
					continue;
				// 获取留言人信息
				Element elAuthor = el.select("a.head_u50").get(0);

				String homeUrl = elAuthor.attr("href");
				String imgUrl = elAuthor.select("img").get(0).attr("src");
				if (!imgUrl.startsWith("http://")) {
					imgUrl = "http://group.worlduc.com" + imgUrl;
				}
				Element ev = el.select("div.gs_viewcont").get(0);
				String authorName = ev.select("a.gs_group").text();

				String location = "";
				String time = ev.select("span.time").text();
				String wordText = ev.select("div.w_90").html();

				Person author = new Person(authorName, imgUrl, homeUrl,
						location);
				LeaveWord leaveWord = new LeaveWord(wordId, wordText, time,
						author);
				list.add(leaveWord);

				// debugPrint(leaveWord.toString());
			}
			Element epager = doc.getElementById("DivPage2");
			if (epager != null) {
				String pageData = epager.text();
				int x = pageData.indexOf("共");
				if (x >= 0) {
					pageData = pageData.substring(x + 1);
					x = pageData.indexOf("页");
					pageData = pageData.substring(0, x);
					int pageCount = Integer.parseInt(pageData);
					pager.setPageCount(pageCount);
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "---getLeaveWordListByTopic---" + url);
			e.printStackTrace();
		}

	}

	public static synchronized void getLeaveWordListByBlogArticle(
			PagerModel<LeaveWord> pager, String bid, String tid) {
		List<LeaveWord> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		if(TextUtils.isEmpty(tid))
			tid = "0";
		int iPage = pager.getCurPage();
		String url = "http://www.worlduc.com/Ajax/Ajax_Comment.aspx";
		try {
			if (pager.getPageCount() <= 1) {
				Connection.Response res = Jsoup.connect(url).cookies(cookies)
						.timeout(TIME_OUT * 10)
						.data("op", "GetBlogCommentCount").data("bid", bid)
						.data("tid", tid).method(Method.POST).execute();
				String  doc = res.body();
				//System.out.println(doc);
				try{
					int x = Integer.parseInt(doc);
					int n = x % WorlducCfg.PAGE_SIZE_WORD_LIST ;
					pager.setPageCount(x / WorlducCfg.PAGE_SIZE_WORD_LIST );
					if(n!=0)
						pager.setPageCount(pager.getPageCount()+1);
				}catch(Exception ex){
					
				}
			}
			//System.out.println("pagecount="+pager.getPageCount() + "|"+pager.getCurPage());
			Connection.Response res = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10)
					.data("op", "BindBlogComment")
					.data("bid", bid)
					.data("tid", tid)
					.data("type","1")
					.data("page",pager.getCurPage()+"")
					.method(Method.POST).execute();
			Document  doc = res.parse();		
			//System.out.println(doc.html());
			// cookies = connection.response().cookies();
			Elements links = doc.select("div.comment_li");
			for (Element el : links) {
				// leaveword3563240
				if(pager.getCurPage()>1 && el.attr("class").contains("topComment"))
					continue;
				if (TextUtils.isEmpty(el.id()))
					break;
				// Log.i(TAG, "--"+el.outerHtml()+"--");
				String wordId = el.id().substring(9);
				Elements xx = el.select("a.comment_li_headimg");
				if (xx == null)
					continue;
				// 获取留言人信息
				Element elAuthor = el.select("a.comment_li_headimg").get(0);

				String homeUrl = elAuthor.attr("href");
				String imgUrl = elAuthor.select("img").get(0).attr("src");
				String authorName = elAuthor.select("img").get(0).attr("alt");

				if (!imgUrl.startsWith("http://")) {
					imgUrl = "http://www.worlduc.com" + imgUrl;
				}
				Element ev = el.select("div.comment_content").get(0);

				String location = "";
				String time = ev.select("span.comment_time").text();
				String wordText = ev.html();
				wordText = wordText.replaceAll("回复……", "");
				
				wordText = wordText.replaceAll("<a.*?onclick=(\"|')DeleteBlogCommentReply.*?(\"|').*?>.*?</a>", "");
				System.out.println(wordText);
				Person author = new Person(authorName, imgUrl, homeUrl,
						location);
				LeaveWord leaveWord = new LeaveWord(wordId, wordText, time,
						author);
				list.add(leaveWord);

				//debugPrint(leaveWord.toString());
			}
			

		} catch (Exception e) {
			Log.e(TAG, "---getLeaveWordListByBlogArticle---" + url);
			e.printStackTrace();
		}

	}

	/**
	 * 回复群组主题
	 * 
	 * @param message
	 *            回复内容
	 * @param topicId
	 *            主题ID
	 * @return
	 */
	public static boolean replyGroupTopic(String message, String topicId) {
		/*
		 * 
		 * http://group.worlduc.com/GroupShow/TopicReply.aspx?tid=4435555
		 * Content <img
		 * src="http://www.worlduc.com/kindeditor401/plugins/emoticons/images/0.gif"
		 * alt="" border="0" />,好好学习！ btnTopicReply 回复 txt_Topic_Reply
		 * <p>OK<br/></p>
		 */
		boolean bOk = false;
		if (!isNetConnected())
			return bOk;
		String url = "http://group.worlduc.com/GroupShow/TopicReply.aspx?Page=1000&order=2&tid="
				+ topicId;

		try {
			Document doc = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10).get();
			String viewState = doc.getElementById("__VIEWSTATE").attr("value");
			String eventState = doc.getElementById("__EVENTVALIDATION").attr(
					"value");

			Connection.Response res = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10).data("__VIEWSTATE", viewState)
					.data("__EVENTVALIDATION", eventState)
					.data("btnTopicReply", "回复")
					.data("txt_Topic_Reply", message).method(Method.POST)
					.execute();
			doc = res.parse();
			String str = message.substring(0, message.length() > 5 ? 5
					: message.length());
			if (res.body().indexOf(str) >= 0) {
				return true;
			}

			// debugPrint("--replyGroupTopic--" + bOk + "|" + res.body());

		} catch (Exception e) {
			Log.e(TAG, "---replyGroupTopic---" + url);
			e.printStackTrace();
		}
		return bOk;
	}
	/**
	 * 对日志文章进行评论
	 * @param message
	 * @param blogId
	 * @return
	 */
	public static boolean replyBlogArticle(String message, String blogId) {

		boolean bOk = false;
		if (!isNetConnected())
			return bOk;
		String url = "http://www.worlduc.com/Ajax/Ajax_Comment.aspx";
		//System.out.println(blogId + "|" + message);
		try {
			
			Connection.Response res = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10)
					.data("op", "SubmitBlogComment")
					.data("bid", blogId)
					.data("content", message).method(Method.POST)
					.execute();
			if (res.body().equals("2")) {
				return true;
			}

			// debugPrint("--replyBlogArticle--" + bOk + "|" + res.body());

		} catch (Exception e) {
			Log.e(TAG, "---replyBlogArticle---" + url);
			e.printStackTrace();
		}
		return bOk;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 获取我的教研室列表
	 * 
	 * @param pager
	 */
	public static synchronized void getTeachingGroupList(
			PagerModel<TeachingGroup> pager, boolean bRefresh) {
		// http://group.worlduc.com/UserCenter/MyGroup.aspx?type=2&Page=1
		String url = "http://group.worlduc.com/UserCenter/MyGroup.aspx?type=2&Page=%s";
		int iPage = pager.getCurPage();
		url = String.format(url, iPage);
		List<TeachingGroup> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		File f = fileCache.getFile(url);
		if (!bRefresh && checkNetStatus() == NetStatus.mobile) {
			try {
				Object obj = readObject(f);
				if (obj != null) {
					List<TeachingGroup> tg = (List<TeachingGroup>) obj;
					if (tg.size() > 0) {
						list.addAll(tg);
						Log.i(TAG, "---getTeachingGroupList--readObject-" + url);
						return;
					}
				}
			} finally {
			}
		}
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			// cookies = connection.response().cookies();
			Element root = doc
					.getElementById("ContentPlaceHolderContentMain_GroupList");
			if (root == null)
				return;
			Elements nodes = root.select("li.grouplist_li");
			for (Element node : nodes) {
				Element a = node.select("li > a").get(0);
				String homeUrl = a.attr("href");
				String name = a.attr("title");
				Element img = a.select("img").get(0);
				String picUrl = img.attr("src");
				picUrl = picUrl.replace('\\', '/');
				TeachingGroup tg = new TeachingGroup(name, homeUrl, picUrl);
				list.add(tg);
				// Log.i(TAG,tg.toString());
			}
			Element epager = doc
					.getElementById("ContentPlaceHolderContentMain_DivPage");
			if (epager != null) {
				String pageData = epager.text();
				int x = pageData.indexOf("共");
				if (x >= 0) {
					pageData = pageData.substring(x + 1);
					x = pageData.indexOf("页");
					pageData = pageData.substring(0, x);
					int pageCount = Integer.parseInt(pageData);
					pager.setPageCount(pageCount);
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getTeachingGroupList---" + url);
			ex.printStackTrace();
		}
		if (list.size() > 0) {
			writeObject(list, f);
			Log.i(TAG, "---getTeachingGroupList--writeObject-" + url);
		}
	}

	/********************************************************************************
	 * 
	 * 获取某个教研室的发贴主题列表
	 * 
	 * @param gid
	 *            教研室ID
	 * @param pager
	 * 
	 ********************************************************************************/
	public static synchronized void getTopicListByGroup(String gid,
			PagerModel<TopicArticle> pager, boolean bRefresh) {
		// http://group.worlduc.com/GroupShow/Home.aspx?gid=31239&tclass=0&ttype=0&tgrade=0&order=1&Page=3
		String url = "http://group.worlduc.com/GroupShow/Home.aspx?gid=%s&tclass=0&ttype=0&tgrade=0&order=1&Page=%s";
		int iPage = pager.getCurPage();
		url = String.format(url, gid, iPage);
		List<TopicArticle> list = pager.getList();
		list.clear();
		File f = fileCache.getFile(url);
		if (!bRefresh && checkNetStatus() == NetStatus.mobile) {
			try {
				Object obj = readObject(f);
				if (obj != null) {
					List<TopicArticle> ta = (List<TopicArticle>) obj;
					if (ta.size() > 0) {
						list.addAll(ta);
						Log.i(TAG, "---getTopicListByGroup--readObject-" + url);
						return;
					}
				}
			} finally {
			}
		}
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			// cookies = connection.response().cookies();
			Element root = doc
					.getElementById("ContentPlaceHolderContentMain_divTopicList");
			if (root == null)
				return;
			Elements nodes = root.select("li.list_li");
			for (Element node : nodes) {
				Element el = node.select("div.gs_view_count").get(0);
				String viewCount = el.text();
				el = node.select("div > a.i_poster").get(0);
				String name = el.text();
				String homeUrl = el.attr("href");
				Element tmp = node.select("div.dynamic_content").get(0);
				el = tmp.select("h1 > a").get(0);
				String title = el.text();
				String topicUrl = el.attr("href");
				el = tmp.select("div.dynamic_content_pre > p").get(0);
				String content = el.text();
				el = tmp.select("span.time").get(0);
				String time = el.text().replace("发表", "").trim();
				el = tmp.select("a:containsOwn(回复)").get(0);
				String txt = el.text();
				int i = txt.indexOf('(');
				txt = txt.substring(i + 1);
				i = txt.indexOf(')');
				String replyCount = txt.substring(0, i);
				TopicArticle ta = new TopicArticle(time, topicUrl, title,
						content, viewCount, replyCount);
				Person p = new Person(name, homeUrl);
				ta.setAuthor(p);
				list.add(ta);
				// Log.i(TAG,ta.toString());
			}
			Element epager = doc
					.getElementById("ContentPlaceHolderContentMain_DivPage2");
			if (epager != null) {
				String pageData = epager.text();
				int x = pageData.indexOf("共");
				if (x >= 0) {
					pageData = pageData.substring(x + 1);
					x = pageData.indexOf("页");
					pageData = pageData.substring(0, x);
					int pageCount = Integer.parseInt(pageData);
					pager.setPageCount(pageCount);
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getTopicListByGroup---" + url);
			ex.printStackTrace();
		}
		if (list.size() > 0) {
			writeObject(list, f);
			Log.i(TAG, "---getTopicListByGroup--writeObject-" + url);
		}
	}

	/**
	 * 获取群组主题的内容
	 * 
	 * @param url
	 * @return
	 */
	public static String getTopicContent(String url) {
		String content = "";
		if (!isNetConnected()) {
			return content;
		}
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();

			Element root = doc.select("div.gs_topic_content").get(0);
			if (root != null) {
				content = root.html();
				// debugPrint("----getTopicContent----"+content);
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getTopicContent---" + url);
			ex.printStackTrace();
		}
		return content;
	}

	public static String getBlogArticleContent(String url) {
		String content = "";
		if (!isNetConnected()) {
			return content;
		}
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();

			Element root = doc.getElementById("divContent");
			if (root != null) {
				content = root.html();
				// debugPrint("----getTopicContent----"+content);
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getBlogArticleContent---" + url);
			ex.printStackTrace();
		}
		return content;
	}

	/**
	 * 获取通知消息列表
	 * 
	 * @param pager
	 */
	public static void getNoticeMessageList(PagerModel<NoticeMessage> pager) {
		// http://www.worlduc.com/SpaceManage/Notice/NoticeList.aspx?Page=1
		int iPage = pager.getCurPage();
		String url = "http://www.worlduc.com/SpaceManage/Notice/NoticeList.aspx?Page=%d";
		url = String.format(url, iPage);
		List<NoticeMessage> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			// cookies = connection.response().cookies();
			Element root = doc.getElementById("div_wrapper_main_1");
			if (root == null)
				return;
			Elements nodes = root.select("ul > li.list");
			if (nodes == null)
				return;
			for (Element node : nodes) {
				String text = node.text();
				String html = node.html();
				html = html.replaceAll(
						"<span.*?class=(\"|')time(\"|').*?>.*?</span>", "");
				String time = node.select("span.time").text();
				NoticeMessage msg = new NoticeMessage(html, text, time);
				list.add(msg);
				// debugPrint(html);
			}
			Element epager = doc
					.getElementById("ctl00_ContentPlaceHolderMain_DivPage");
			if (epager != null) {
				String pageData = epager.text();
				int x = pageData.indexOf("共");
				if (x >= 0) {
					pageData = pageData.substring(x + 1);
					x = pageData.indexOf("页");
					pageData = pageData.substring(0, x);
					int pageCount = Integer.parseInt(pageData);
					pager.setPageCount(pageCount);
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getNoticeMessageList---" + url);
			ex.printStackTrace();
		}
	}

	/**
	 * 获取好友请求列表
	 * 
	 * @param pager
	 */
	public static void getFriendRequestList(PagerModel<FriendRequest> pager) {
		// http://www.worlduc.com/SpaceManage/Notice/NoticeList.aspx?Page=1
		int iPage = pager.getCurPage();
		String url = "http://www.worlduc.com/SpaceManage/Notice/RequestList.aspx?Page=%d";
		url = String.format(url, iPage);
		List<FriendRequest> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			// cookies = connection.response().cookies();
			Element root = doc.getElementById("div_wrapper_main_1");
			if (root == null)
				return;
			Elements nodes = root.select("li.comment_li");
			if (nodes == null)
				return;
			for (Element node : nodes) {
				// fq_14826025
				// Gr_xxxxxx //打招呼

				String reqId = node.id().substring(3);
				Element h = node.select(".headImage_1").first();
				String homeUrl = h.attr("href");
				String name = h.select("img").attr("alt");
				String imgUrl = h.select("img").attr("src");

				h = node.select("div.comment_li_content").first();
				String time = h.select("span.time").text();
				String remark = h.select("span[name=requestFy]").text();
				String loc = h.ownText();
				FriendRequest fr = new FriendRequest(reqId, name, imgUrl,
						homeUrl, loc, time, remark);
				if (node.id().startsWith("Gr_")) {
					fr.setGreet(true);
				}
				;
				list.add(fr);
				// debugPrint(node.text());
			}
			Element epager = doc
					.getElementById("ctl00_ContentPlaceHolderMain_DivPage");
			if (epager != null) {
				String pageData = epager.text();
				int x = pageData.indexOf("共");
				if (x >= 0) {
					pageData = pageData.substring(x + 1);
					x = pageData.indexOf("页");
					pageData = pageData.substring(0, x);
					int pageCount = Integer.parseInt(pageData);
					pager.setPageCount(pageCount);
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getFriendRequestList---" + url);
			ex.printStackTrace();
		}
	}

	/**
	 * 获取好友分组
	 * 
	 * @return
	 */
	public static List<FriendGroup> getFriendGroupList(boolean bRefresh) {
		List<FriendGroup> list = new Vector<FriendGroup>();
		list.clear();
		if (!isNetConnected())
			return list;
		// http://www.worlduc.com/SpaceManage/Ajax/Ajax_Friend.aspx
		String url = "http://www.worlduc.com/SpaceManage/Ajax/Ajax_Friend.aspx";
		File f = fileCache.getFile(url);
		if (!bRefresh && checkNetStatus() == NetStatus.mobile) {
			try {
				Object obj = readObject(f);
				if (obj != null) {
					List<FriendGroup> fg = (List<FriendGroup>) obj;
					if (fg.size() > 0) {
						list.addAll(fg);
						Log.i(TAG, "---getFriendGroupList--readObject-" + url);
						return list;
					}
				}
			} finally {
			}
		}

		list.add(new FriendGroup("0", "未分组 "));
		try {
			Connection.Response res = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10).data("op", "GetFriendGroupList")
					.method(Method.POST).execute();
			Document doc = res.parse();
			Elements nodes = doc.select("option");
			for (Element node : nodes) {
				FriendGroup fg = new FriendGroup(node.attr("value"),
						node.text());
				list.add(fg);
			}
			// debugPrint(doc.html());
		} catch (Exception ex) {
			Log.e(TAG, "---getFriendGroupList---" + url);
			ex.printStackTrace();
		}
		if (list.size() > 0) {
			writeObject(list, f);
			Log.i(TAG, "getFriendGroupList write ---" + f.getName());
		}
		return list;
	}

	/**
	 * 同意好友请求
	 * 
	 * @param fr
	 * @param fgId
	 * @return
	 */
	public static boolean acceptFriendRequest(FriendRequest fr, String fgId) {
		// op=AcceptFriendRequest&requestid=14861646&friendid=608604&friendgroupid=556597
		// &friendgroupname=&remarksname=1233%u5510%u60A6%u73AE&flag=0&isnew=false
		// http://www.worlduc.com/SpaceManage/Ajax/Ajax_Friend.aspx
		if (!isNetConnected())
			return false;
		if (fr.isGreet())
			return agreeGreet(fr.getUid());
		String url = "http://www.worlduc.com/SpaceManage/Ajax/Ajax_Friend.aspx";
		try {
			Connection.Response res = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10).data("op", "AcceptFriendRequest")
					.data("requestid", fr.getReqId())
					.data("friendid", fr.getUid()).data("friendgroupid", fgId)
					.data("friendgroupname", "")
					.data("remarksname", fr.getName()).data("flag", "0")
					.data("isnew", "false").method(Method.POST).execute();
			if (res.body().indexOf("2") >= 0)
				return true;
			// debugPrint(res.body());
		} catch (Exception ex) {
			Log.e(TAG, "---acceptFriendRequest---" + url);
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取微博列表
	 * 
	 * @param pager
	 */
	public static void getMiniBlogList(PagerModel<MiniBlog> pager) {
		int iPage = pager.getCurPage();
		String url = "http://www.worlduc.com/SpaceManage/MiniBlog/MiniBlog.aspx?Page=%d";
		url = String.format(url, iPage);
		List<MiniBlog> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			Element root = doc.getElementById("miniblg");
			if (root == null)
				return;
			Elements nodes = root.children();
			if (nodes == null)
				return;
			for (Element node : nodes) {
				// li_1150958
				String id = node.id().substring(3);
				String mood = node.select("span.minitype").text();
				String time = node.select("span.time").text();
				String content = node.select("div[style]").html();
				MiniBlog mb = new MiniBlog(id, content, time);
				list.add(mb);
				// debugPrint(node.text());
			}
			Element epager = doc
					.getElementById("ctl00_ContentPlaceHolderMain_DivPage");
			if (epager != null) {
				String pageData = epager.text();
				int x = pageData.indexOf("共");
				if (x >= 0) {
					pageData = pageData.substring(x + 1);
					x = pageData.indexOf("页");
					pageData = pageData.substring(0, x);
					int pageCount = Integer.parseInt(pageData);
					pager.setPageCount(pageCount);
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getMiniBlogList---" + url);
			ex.printStackTrace();
		}
	}

	/**
	 * 发微博
	 * 
	 * @param content
	 * @param mood
	 * @param category
	 * @return
	 */
	public static boolean publishMinoBlog(String content, String mood,
			String category) {
		String url = "http://www.worlduc.com/SpaceManage/MiniBlog/MiniBlog.aspx";
		boolean bOk = false;
		if (!isNetConnected())
			return bOk;
		try {
			Document doc = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10).get();
			String viewState = doc.getElementById("__VIEWSTATE").attr("value");
			String eventState = doc.getElementById("__EVENTVALIDATION").attr(
					"value");

			Connection.Response res = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10).data("__VIEWSTATE", viewState)
					.data("__EVENTVALIDATION", eventState).data("mType", mood)
					.data("mMenu", category)
					.data("ctl00$ContentPlaceHolderMain$Textarea1", content)
					.data("ctl00$ContentPlaceHolderMain$txtCheckCode", "")
					.data("ctl00$ContentPlaceHolderMain$Button1", "发布")
					.method(Method.POST).execute();
			doc = res.parse();
			String str = content.substring(0, content.length() > 5 ? 5
					: content.length());
			Element elReplys = doc.getElementById("miniblg");
			if (elReplys != null && elReplys.text().indexOf(str) >= 0) {
				bOk = true;
			}
			// debugPrint("--publishMinoBlog--" + bOk + "|" + elReplys);

		} catch (Exception e) {
			Log.e(TAG, "---publishMinoBlog---" + url);
			e.printStackTrace();
		}
		return bOk;
	}

	/**
	 * 获取分组中的好友
	 * 
	 * @param pager
	 * @param gid
	 */
	public static void getFriendListByGroup(PagerModel<Person> pager,
			String gid, boolean bRefresh) {
		// http://www.worlduc.com/Social/Friend/Batch.aspx?gid=556597
		int iPage = pager.getCurPage();
		String url = "http://www.worlduc.com/Social/Friend/Batch.aspx?gid=%s&Page=%s";
		url = String.format(url, gid, iPage);
		List<Person> list = pager.getList();
		list.clear();
		if (!isNetConnected())
			return;
		File f = fileCache.getFile(url);
		if (!bRefresh && checkNetStatus() == NetStatus.mobile) {
			try {
				Object obj = readObject(f);
				if (obj != null) {
					List<Person> p = (List<Person>) obj;
					if (p.size() > 0) {
						list.addAll(p);
						Log.i(TAG, "---getFriendListByGroup--readObject-" + url);
						return;
					}
				}
			} finally {
			}
		}
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			Element root = doc
					.getElementById("ctl00_ContentPlaceHolderMain_friendList");
			if (root == null)
				return;

			Elements nodes = root.select(".friend_Div");
			if (nodes == null)
				return;
			for (Element node : nodes) {
				String id = node.attr("fdata");
				Element img = node.select("img").first();
				String imgUrl = img.attr("src");
				String name = img.attr("alt");
				Person p = new Person(id, name, imgUrl);
				list.add(p);
				debugPrint(p.toString() + "|" + p.getHomeUrl());
			}
			// if(pager.getPageSize()>1) return;
			Element epager = doc
					.getElementById("ctl00_ContentPlaceHolderMain_DivPage");
			if (epager != null) {
				String pageData = epager.text();
				int x = pageData.indexOf("共");
				if (x >= 0) {
					pageData = pageData.substring(x + 1);
					x = pageData.indexOf("页");
					pageData = pageData.substring(0, x);
					int pageCount = Integer.parseInt(pageData);
					pager.setPageCount(pageCount);
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getFriendListByGroup---" + url);
			ex.printStackTrace();
		}
		if (list.size() > 0) {
			writeObject(list, f);
			Log.i(TAG, "---getFriendListByGroup--writeObject-" + url);

		}
	}

	/**
	 * 给好友留言
	 * 
	 * @param content
	 *            留言内容
	 * @param uid
	 *            好友ID
	 * @return
	 */
	public static boolean publishLeaveWord(String content, String uid) {
		// op=AddLeaveWord&receiver={0}&Content={1}
		// http://www.worlduc.com/SpaceManage/Ajax/Ajax_LeaveWord.aspx";
		String url = "http://www.worlduc.com/SpaceManage/Ajax/Ajax_LeaveWord.aspx";
		boolean bOk = false;
		if (!isNetConnected())
			return false;
		try {

			Connection.Response res = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10).data("op", "AddLeaveWord")
					.data("receiver", uid).data("Content", content)
					.method(Method.POST).execute();
			Document doc = res.parse();
			String str = content.substring(0, content.length() > 5 ? 5
					: content.length());
			if (doc != null && doc.body().text().indexOf(str) >= 0) {
				bOk = true;
			}
			debugPrint("--publishLeaveWord--" + bOk + "|" + doc.body().html());

		} catch (Exception e) {
			Log.e(TAG, "---publishLeaveWord---" + url);
			e.printStackTrace();
		}
		return bOk;
	}

	public static List<Category> getBlogCategories(String uid) {
		List<Category> list = new ArrayList<Category>();
		if (TextUtils.isEmpty(uid))
			uid = myUid;
		if (!isNetConnected())
			return list;
		String url = "http://www.worlduc.com/SpaceShow/Index.aspx?uid=%s";
		url = String.format(url, uid);
		File f = fileCache.getFile(url);
		if (checkNetStatus() == NetStatus.mobile) {
			Object obj = readObject(f);
			if (obj != null) {
				try {
					List<Category> clist = (List<Category>) obj;
					list.addAll(clist);
					Log.i(TAG, "getBlogCategories read ---" + f.getName());
					return list;
				} finally {
				}
			}
		}
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			Element root = doc.getElementById("CustomMenu_Content");
			if (root == null)
				return list;

			Elements nodes = root.select(".AMenuTitle");
			if (nodes == null)
				return list;
			for (Element node : nodes) {
				String id = node.id().substring(8);
				String name = node.text();
				Element sub = node.nextElementSibling();
				Category category = new Category(id, name, "");
				list.add(category);
				if (sub == null)
					continue;
				Elements snodes = sub.select("li > a");
				debugPrint(id + "|" + name + ">" + sub.text());
				for (Element sn : snodes) {
					String uri = sn.attr("href");
					name = sn.text();
					Category sc = new Category(null, name, uri);
					category.addSubCategory(sc);
					debugPrint(sc.toString());
				}

			}

		} catch (Exception ex) {
			Log.e(TAG, "---getBlogCategories---" + url);
			ex.printStackTrace();
		}
		if (list.size() > 0) {
			writeObject(list, f);
			Log.i(TAG, "getBlogCategories write ---" + url);
		}
		return list;
	}

	public static List<Category> getBlogCategories() {
		List<Category> list = new ArrayList<Category>();

		String url = "http://www.worlduc.com/SpaceManage/Column.aspx";
		/**
		 * 做一个文件缓存
		 */
		File f = fileCache.getFile(url);
		if (checkNetStatus() == NetStatus.mobile) {
			Object obj = readObject(f);
			if (obj != null) {
				try {
					List<Category> clist = (List<Category>) obj;
					list.addAll(clist);
					Log.i(TAG, "getBlogCategories read ---" + f.getName());
					return list;
				} finally {
				}
			}
		}
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			Element root = doc
					.getElementById("ctl00_ContentPlaceHolderMain_div_content_1");
			if (root == null)
				return list;

			Elements nodes = root.select(".headline");
			if (nodes == null)
				return list;
			for (Element node : nodes) {
				String name = node.select("h3").text();
				Element sub = node.nextElementSibling();
				String id = sub.id().substring(5);
				Category category = new Category(id, name, "");
				list.add(category);
				if (sub == null)
					continue;
				Elements snodes = sub.select("li > a.fl");
				// debugPrint(id + "|" + name + ">"+sub.text());
				for (Element sn : snodes) {
					String uri = sn.attr("href");
					name = sn.text();
					Category sc = new Category(null, name, uri);
					category.addSubCategory(sc);
					// debugPrint(sc.toString());
				}
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getBlogCategories---" + url);
			ex.printStackTrace();
		}
		if (list.size() > 0) {
			writeObject(list, f);
			Log.i(TAG, "getBlogCategories write ---" + f.getName());
		}
		return list;
	}
	
	public static List<SubCategory> getBlogSubCategories(String uid, String sid){
		if (TextUtils.isEmpty(uid))
			uid = myUid;
		//二级栏目所包含的类别列表
		List<SubCategory> list = new ArrayList<SubCategory>();
        String url = "http://www.worlduc.com/SpaceShow/Blog/List.aspx?sid=%s&uid=%s";
        url = String.format(url, sid,uid);
        if (!isNetConnected())
			return list;
        
        /**
		 * 做一个文件缓存
		 */
		File f = fileCache.getFile(url);
		if (checkNetStatus() == NetStatus.mobile) {
			Object obj = readObject(f);
			if (obj != null) {
				try {
					List<SubCategory> clist = (List<SubCategory>) obj;
					list.addAll(clist);
					Log.i(TAG, "getBlogSubCategories read ---" + f.getName());
					return list;
				} finally {
				}
			}
		}
		try {
			Connection connection = Jsoup.connect(url).cookies(cookies)
					.timeout(TIME_OUT * 10);
			Document doc = connection.get();
			Element root = doc
					.getElementById("div_wrapper_main_1");
			if (root == null)
				return list;

			Elements nodes = root.select("div.caption");
			if (nodes == null)
				return list;
			for (Element node : nodes) {
				String cname = node.select("span").first().text();
                String categoryUrl = node.select("a").attr("href");
                SubCategory bc = new SubCategory(categoryUrl, cname);
                list.add(bc);
			}
		} catch (Exception ex) {
			Log.e(TAG, "---getBlogSubCategories---" + url);
			ex.printStackTrace();
		}
		if (list.size() > 0) {
			writeObject(list, f);
			Log.i(TAG, "getBlogSubCategories write ---" + f.getName());
		}
		return list;
	}
	
	public static List<BlogArticle> GetBlogArticleListBySIdCId(SubCategory sc){
		List<BlogArticle> list= new ArrayList<BlogArticle>();
		String url = "http://www.worlduc.com/SpaceShow/Blog/More.aspx?cid=%s&sid=%s&uid=%s";
		url = String.format(url, sc.getCid(),sc.getSid(),sc.getUid());
		if (!isNetConnected())
			return list;
        
        /**
		 * 做一个文件缓存
		 */
		File f = fileCache.getFile(url);
		if (checkNetStatus() == NetStatus.mobile) {
			Object obj = readObject(f);
			if (obj != null) {
				try {
					List<BlogArticle> clist = (List<BlogArticle>) obj;
					list.addAll(clist);
					Log.i(TAG, "GetBlogArticleListBySIdCId read ---" + f.getName());
					return list;
				} finally {
				}
			}
		}
		try {
			
			//得到分页参数
            int pages = 1;
            int curPage = 1;
            do
            {
            	Connection connection = Jsoup.connect(url+ "&Page="+curPage).cookies(cookies)
    					.timeout(TIME_OUT * 10);
    			Document doc = connection.get();
    			
                
                if (curPage == 1)
                {
                	Element root = doc.getElementById("ctl00_ContentPlaceHolderMain_DivPage");
                	//获取页数
                    if (root != null)
                    {
                        String tmp = root.text();
                        int x = tmp.indexOf("共");
                        tmp = tmp.substring(x + 1);
                        x = tmp.indexOf("页");
                        tmp = tmp.substring(0, x);
                        pages = Integer.parseInt(tmp);
                    }                   
                }
                Element rootNode = doc.getElementById("div_wrapper_main_1").select("ul.blog_list").first();
                Elements nodeList = rootNode.select("a");
                if (nodeList != null)
                {
                    for (Element node : nodeList)
                    {
                        String title = node.text().trim();
                        String href = node.attr("href");
                        BlogArticle ba = new BlogArticle(sc.getUid(), href);
                        ba.setTitle(title);
                        list.add(ba);                        
                        //获取博文内容                      
                    }
                }
                curPage++;
            } while (curPage <= pages);
		} catch (Exception ex) {
			Log.e(TAG, "---GetBlogArticleListBySIdCId---" + url);
			ex.printStackTrace();
		}
		if (list.size() > 0) {
			writeObject(list, f);
			Log.i(TAG, "GetBlogArticleListBySIdCId write ---" + f.getName());
		}
		return list;
	}

	public static boolean PostArticle(String categoryId, String checkCode,
			String title, String tag, String content) {
		if (!isNetConnected())
			return false;
		String url = "http://www.worlduc.com/SpaceManage/Blog/PostBlog.aspx?sid=%s";
		url = String.format(url, categoryId);
		String boundary = "-----------------------------"
				+ Long.toHexString(System.currentTimeMillis());
		Log.i(WorlducCfg.TAG, "url:" + url);
		// 获取发文章界面
		try {
			Document doc = Jsoup.connect(url).cookies(cookies)
					.ignoreContentType(true).timeout(TIME_OUT * 10).get();
			String viewState = doc.getElementById("__VIEWSTATE").attr("value");
			String eventState = doc.getElementById("__EVENTVALIDATION").attr(
					"value");

			// 组织表单数据
			StringBuilder sb = new StringBuilder();
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"__VIEWSTATE\"\r\n");
			sb.append("\r\n");
			sb.append(viewState + "\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"__EVENTVALIDATION\"\r\n");
			sb.append("\r\n");

			sb.append(eventState + "\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$txtStyleStr\"\r\n");
			sb.append("\r\n");
			sb.append("\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$hdfBlogStyle\"\r\n");
			sb.append("\r\n");
			sb.append("0\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$TitleTxt\"\r\n");
			sb.append("\r\n");
			sb.append(title + "\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$TagTxt\"\r\n");
			sb.append("\r\n");
			sb.append(tag + "\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$CustomItemDownList\"\r\n");
			sb.append("\r\n");
			sb.append("0\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$TitleMenuDownList\"\r\n");
			sb.append("\r\n");
			sb.append(83 + "\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$ContenEditor\"\r\n");
			sb.append("\r\n");
			sb.append(content + "\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$ckbIsAllowComment\"\r\n");
			sb.append("\r\n");
			sb.append("on\r\n");
			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$DDLCommentPrivacy$setPrivacy\"\r\n");
			sb.append("\r\n");
			sb.append("0\r\n");

			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$DDLCommentPrivacy$HidFriendGroupID\"\r\n");
			sb.append("\r\n");
			sb.append("\r\n");

			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$DDLCommentPrivacy$HidSelectPeopleJson\"\r\n");
			sb.append("\r\n");
			sb.append("\r\n");

			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$DDLCommentPrivacy$HidSelectPeople\"\r\n");
			sb.append("\r\n");
			sb.append("\r\n");

			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$File1\"; filename=\"\"\r\n");
			sb.append("Content-Type: application/octet-stream\r\n");
			sb.append("\r\n");
			sb.append("\r\n");

			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$txtCheckCode\"\r\n");
			sb.append("\r\n");
			sb.append(checkCode + "\r\n");

			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$SaveBtn\"\r\n");
			sb.append("\r\n");
			sb.append(URLEncoder.encode("发表", "UTF-8") + "\r\n");

			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$SpaceIDHidden\"\r\n");
			sb.append("\r\n");
			sb.append(categoryId + "\r\n");

			sb.append(boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"ctl00$ContentPlaceHolderMain$UserIDHidden\"\r\n");
			sb.append("\r\n");
			sb.append(myUid + "\r\n");

			sb.append(boundary + "--\r\n");

			URL uri = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();

			conn.setReadTimeout(TIME_OUT * 10);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Referer", url);
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + boundary.substring(2));
			conn.setRequestProperty("User-Agent", USER_AGENT);
			String cookiesStr = "";
			for (Entry<String, String> cookie : cookies.entrySet()) {
				if (cookiesStr.length() == 0)
					cookiesStr += cookie.getKey() + "=" + cookie.getValue();
				else
					cookiesStr += ";" + cookie.getKey() + "="
							+ cookie.getValue();

			}
			conn.addRequestProperty("Cookie", cookiesStr);

			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			outStream.flush();

			// 得到响应码
			int res = conn.getResponseCode();
			if (res == 200) {
				InputStream in = conn.getInputStream();
				doc = Jsoup.parse(in, "UTF-8", "http://www.worlduc.com");
				// in.close();
				// conn.disconnect();
				System.out.println("res:" + doc.text());
				if (doc.title().contains("文章发表")) {
					if (!doc.body().html().contains("验证码错误")) {
						System.out.println("OK");
						return true;
					} else {
						throw new Exception("验证码错误");
					}
				}
			}
		} catch (Exception ex) {
			Log.i(WorlducCfg.TAG, "PostArticle----" + ex.getMessage());
			ex.printStackTrace();
		} finally {

		}
		return false;
	}

	private static void writeObject(Object obj, File f) {
		try {
			FileOutputStream os = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(obj);
			oos.close();
			os.close();
		} catch (IOException e) {
			Log.i(TAG, "writeObject error!");
			e.printStackTrace();
		}
	}

	private static Object readObject(File f) {

		try {
			InputStream is = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(is);
			Object obj = ois.readObject();
			ois.close();
			return obj;
		} catch (Exception e) {
			Log.i(TAG, "readObject error!");
			e.printStackTrace();
		}
		return null;
	}

	private static void debugPrint(String msg) {
		Log.i(TAG, "----------------------------------------------");
		Log.i(TAG, msg);
		Log.i(TAG, "----------------------------------------------");
	}
}
