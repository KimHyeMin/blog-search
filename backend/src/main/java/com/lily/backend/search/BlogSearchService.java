package com.lily.backend.search;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.lily.backend.blog.FavoriteBlogService;
import com.lily.backend.search.client.BlogSearchClientWrapper;
import com.lily.backend.search.dto.BlogDocument;
import com.lily.backend.search.dto.BlogSearchResult;
import com.lily.backend.search.request.BlogSearchRequest;
import com.lily.backend.security.UserDetailsImpl;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class BlogSearchService {

  @Autowired
  private BlogSearchClientWrapper blogSearchClient;

  @Autowired
  private FrequentKeywordService frequentKeywordService;

  @Autowired
  private FavoriteBlogService favoriteBlogService;

  public BlogSearchResult searchBlogs(final BlogSearchRequest request, final UserDetailsImpl user) {

    if (request.isFirst()) {
      frequentKeywordService.marking(request.getKeywords());
    }

    BlogSearchResult blogSearchResult = blogSearchClient.search(request);

    injectFavoriteBlogId(user.getId(), blogSearchResult.getBlogList());
    return blogSearchResult;

  }

  private void injectFavoriteBlogId(Long userId, List<BlogDocument> blogList) {
    List<Integer> urlHashCodes = blogList.stream().map(BlogDocument::getUrlHashCode).toList();

    Map</* url hashcode*/Integer, /* favoriteId */Long> favoriteIdMap = favoriteBlogService.findFavoriteIds(userId, urlHashCodes);

    for (BlogDocument blogDocument : blogList) {

      Long favoriteId = favoriteIdMap.get(blogDocument.getUrlHashCode());
      if (!isEmpty(favoriteId)) {
        blogDocument.setFavoriteId(favoriteId);
      }
    }
  }

}
