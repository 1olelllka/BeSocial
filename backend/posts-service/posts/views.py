from rest_framework import viewsets
from rest_framework.response import Response
from rest_framework.decorators import api_view
from rest_framework.generics import DestroyAPIView
from .models import Post, Comment
from .serializers import PostSerializer, CommentSerializer
from django.shortcuts import get_object_or_404


class UserPostViewSet(viewsets.ModelViewSet):
    queryset = Post.objects.all().order_by('-created_at')
    serializer_class = PostSerializer

    def create(self, request, user_id):
        request.data['user_id'] = user_id
        return super().create(request)

    def get_queryset(self):
        return self.queryset.filter(user_id=self.kwargs['user_id'])
    

class PostViewSet(viewsets.ModelViewSet):
    queryset = Post.objects.all().order_by('-created_at')
    serializer_class = PostSerializer

    def retrieve(self, request, *args, **kwargs):
        post = get_object_or_404(self.queryset, pk=kwargs['post_id'])
        serializer = self.get_serializer(post)
        return Response(serializer.data)

    def partial_update(self, request, *args, **kwargs):
        post = get_object_or_404(self.queryset, pk=kwargs['post_id'])
        request.data['user_id'] = post.user_id
        serializer = self.get_serializer(post, data=request.data)
        serializer.is_valid(raise_exception=True)
        self.perform_update(serializer)
        return Response(serializer.data)

    def destroy(self, request, *args, **kwargs):
        try:
            post = Post.objects.get(pk=kwargs['post_id'])
            self.perform_destroy(post)
            return Response(status=204)
        except Post.DoesNotExist:
            return Response(status=204)
        

class CommentViewSet(viewsets.ModelViewSet):
    queryset = Comment.objects.filter(parent=None).order_by('-created_at')
    serializer_class = CommentSerializer

    def get_queryset(self):
        return self.queryset.filter(post_id=self.kwargs['post_id'])

    def create(self, request, post_id):
        request.data['post'] = post_id
        return super().create(request)
    

class CommentDeleteAPIView(DestroyAPIView):
    queryset = Comment.objects.all()
    serializer_class = CommentSerializer
    
    def destroy(self, request, *args, **kwargs):
        try:
            comment = Comment.objects.get(pk=kwargs['comment_id'])
            self.perform_destroy(comment)
            return Response(status=204)
        except Comment.DoesNotExist:
            return Response(status=204)
        
@api_view(["GET"])
def get_posts_by_users(request):
    if request.GET.get('user_ids') is None or request.GET.get('user_ids') == '{}':
        return Response([])
    user_ids = request.GET.get('user_ids')[1:-1].split(',')
    posts = Post.objects.filter(user_id__in=user_ids).order_by('-created_at')
    serializer = PostSerializer(posts, many=True)
    return Response(serializer.data)