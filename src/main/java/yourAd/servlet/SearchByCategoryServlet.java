package yourAd.servlet;

import yourAd.manager.AdManager;
import yourAd.manager.CategoryManager;
import yourAd.model.Category;
import yourAd.pages.Pages;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/category/*")
public class SearchByCategoryServlet extends HttpServlet implements Pages {

    private static final int DEFAULT_SIZE = 6;

    private AdManager adManager;
    private CategoryManager categoryManager;

    @Override
    public void init() throws ServletException {
        adManager = (AdManager) getServletContext().getAttribute("adManager");
        categoryManager = (CategoryManager) getServletContext().getAttribute("categoryManager");
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int categoryId = getCategoryId(req);
        Category category;
        if(categoryId == -1 || (category = categoryManager.getById(categoryId)) == null){
            resp.sendRedirect("/");
            return;
        }
        String pageStr = req.getParameter("page");
        int page = parseToInteger(pageStr);
        int count = adManager.getCountByCategoryId(categoryId);
        int length = getLength(count);
        if(page >= length){
            page = 0;
        }
        req.setAttribute("adList",adManager.getByAllCategoryId(categoryId,categoryManager.getChildrenList(categoryId)));
        req.setAttribute("header","result by category: " + category.getName());
        req.setAttribute("pageNumber",page);
        req.setAttribute("length",length);
        req.setAttribute("categories",categoryManager.getAll());
        req.getRequestDispatcher(INDEX).forward(req,resp);
    }

    private int getCategoryId(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String[] array = requestURI.split("/");
        int id;
        try {
            id = Integer.parseInt(array[2]);
            if(id <= 0){
                id = -1;
            }
        }catch (NumberFormatException e){
            id = -1;
        }
        return id;
    }

    private int parseToInteger(String pageStr){
        if(pageStr == null){
            return 0;
        }
        int page;
        try {
            page = Integer.parseInt(pageStr);
            if(page < 0){
                page = 0;
            }
        }catch (NumberFormatException e){
            page = 0;
        }
        return page;
    }

    private int getLength(int count){
        int length;
        if(count < DEFAULT_SIZE){
            length = 1;
        }else if(count % DEFAULT_SIZE != 0){
            length = (count/DEFAULT_SIZE) + 1;
        }else {
            length = count/DEFAULT_SIZE;
        }
        return length;
    }

}
