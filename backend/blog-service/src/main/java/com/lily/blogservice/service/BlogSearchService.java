package com.lily.blogservice.service;

import static org.springframework.util.ObjectUtils.isEmpty;

import com.lily.blogservice.client.BlogSearchClient;
import com.lily.blogservice.dto.BlogDocument;
import com.lily.blogservice.dto.BlogSearchRequest;
import com.lily.blogservice.dto.BlogSearchResult;
import com.lily.userauth.security.UserDetailsImpl;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class BlogSearchService {

  private final BlogSearchClient blogSearchClient;

  private final FrequentKeywordService frequentKeywordService;

  private final FavoriteBlogService favoriteBlogService;

  public final BlogSearchResult searchBlogs(final BlogSearchRequest request, final UserDetailsImpl user) {

    if (request.isFirst()) {
      frequentKeywordService.marking(request.getKeywords());
    }

    BlogSearchResult blogSearchResult = blogSearchClient.search(request);

    injectFavoriteBlogId(user.getId(), blogSearchResult.getBlogList());
    return blogSearchResult;

  }

  private void injectFavoriteBlogId(Long userId, List<BlogDocument> blogList) {
    List<Integer> urlHashCodes = blogList.stream().map(BlogDocument::getUrlHashCode).collect(
        Collectors.toList());

    Map</* url hashcode*/Integer, /* favoriteId */Long> favoriteIdMap = favoriteBlogService.findFavoriteIds(userId, urlHashCodes);

    for (BlogDocument blogDocument : blogList) {

      Long favoriteId = favoriteIdMap.get(blogDocument.getUrlHashCode());
      if (!isEmpty(favoriteId)) {
        blogDocument.setFavoriteId(favoriteId);
      }
    }
  }

}
