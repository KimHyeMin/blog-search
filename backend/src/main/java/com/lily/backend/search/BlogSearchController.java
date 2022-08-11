package com.lily.backend.search;

import com.lily.backend.common.dto.APIResponse;
import com.lily.backend.search.dto.BlogSearchResult;
import com.lily.backend.search.dto.FrequentKeyword;
import com.lily.backend.search.request.BlogSearchRequest;
import com.lily.backend.search.response.FrequentResult;
import com.lily.backend.security.CurrentUser;
import com.lily.backend.security.UserDetailsImpl;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping(value = "/api/v1/search")
public class BlogSearchController {

  @Autowired
  private BlogSearchService blogSearchService;

  @Autowired
  private FrequentKeywordService frequentKeywordService;

  @GetMapping(value = "/blogs", produces = MediaType.APPLICATION_JSON_VALUE)
  public APIResponse<BlogSearchResult> searchBlogs(@ModelAttribute @Valid final BlogSearchRequest request,
      @CurrentUser UserDetailsImpl user) {

    final BlogSearchResult result = blogSearchService.searchBlogs(request, user);

    return APIResponse.success(result);
  }

  @GetMapping(value = "/frequent/keywords", produces = MediaType.APPLICATION_JSON_VALUE)
  public APIResponse<FrequentResult> getTopKeywords() {
    List<FrequentKeyword> top10 = frequentKeywordService.getList();
    FrequentResult result = new FrequentResult(top10);
    return APIResponse.success(result);
  }

}
