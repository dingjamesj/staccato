#ifndef PLAYLIST_TREE_HPP
#define PLAYLIST_TREE_HPP

#include <string>
#include <vector>
#include <any>
#include <stack>
#include <iterator>

//=====================================================================================================================================================
// A tree structure that stores playlist IDs. Each node represents a folder, and each node contains an ordered list of playlist IDs and other folders.
//=====================================================================================================================================================

namespace staccato {

    class PlaylistTree {

        private:
        
        //A vector of strings and vectors. A string represents a playlist ID, and a vector represents a folder of more playlist IDs and folders.
        //Folders are represented by pair<string, vector<any>>, with pair.first being the folder name and pair.second being its contents.
        std::vector<std::any> root_;

        std::vector<std::any>* find_folder(const std::vector<std::string>& folder_hierarchy);

        std::vector<std::string> get_folder_playlists_recursive(const std::vector<std::any>& folder) const;

        public:

        PlaylistTree();
        PlaylistTree(const PlaylistTree& other) = delete;
        PlaylistTree(PlaylistTree&& other) = delete;
        PlaylistTree& operator=(const PlaylistTree& other) = delete;
        PlaylistTree& operator=(PlaylistTree&& other) = delete;

        /// @brief Mutable iterator for `PlaylistTree`.
        ///        If the iterator is pointing to a playlist ID, then it returns that ID.
        ///        If the iterator is pointing to a folder, then it returns the folder name.
        ///        If the iterator reached the end of a folder, then it returns `nullptr` before returning to the parent folder.
        class Iterator {

            friend class PlaylistTree;

            using iterator_category = std::forward_iterator_tag;
            using value_type = std::string;
            using difference_type = std::ptrdiff_t;
            using pointer = std::string*;
            using reference = std::string&;

            private:

            std::stack<std::pair<std::vector<std::any>&, std::size_t>> todo_;

            Iterator(std::vector<std::any>& init_folder, std::size_t init_index);

            public:

            /// @brief Prefix increment
            Iterator& operator++();
            /// @brief Postfix increment
            Iterator operator++(int);
            /// @brief Return pointer
            pointer operator->() const;
            /// @brief Return dereferenced pointer
            reference operator*() const;
            /// @return `true` if the iterator is at a folder. Note that it points to the folder's name, NOT the folder's first element.
            bool is_folder_start() const;
            /// @return `true` if the iterator just went past the end of a folder. 
            ///         Note that it is `nullptr` here, it does NOT point to the parent folder's next element.
            bool is_folder_end() const;

            bool operator==(const Iterator& other) const;
            bool operator!=(const Iterator& other) const;

        };

        /// @brief Constant iterator for `PlaylistTree`.
        ///        If the iterator is pointing to a playlist ID, then it returns that ID.
        ///        If the iterator is pointing to a folder, then it returns the folder name.
        ///        If the iterator reached the end of a folder, then it returns `nullptr` before returning to the parent folder.
        class ConstIterator {

            friend class PlaylistTree;

            using iterator_category = std::forward_iterator_tag;
            using value_type = std::string;
            using difference_type = std::ptrdiff_t;
            using pointer = const std::string*;
            using reference = const std::string&;

            private:

            std::stack<std::pair<const std::vector<std::any>&, std::size_t>> todo_;

            ConstIterator(const std::vector<std::any>& init_folder, std::size_t init_index);

            public:

            /// @brief Prefix increment
            ConstIterator& operator++();
            /// @brief Postfix increment
            ConstIterator operator++(int);
            /// @brief Return pointer
            pointer operator->() const;
            /// @brief Return dereferenced pointer
            reference operator*() const;
            /// @return `true` if the iterator is at a folder. Note that it points to the folder's name, NOT the folder's first element.
            bool is_folder_start() const;
            /// @return `true` if the iterator just went past the end of a folder. 
            ///         Note that it is `nullptr` here, it does NOT point to the parent folder's next element.
            bool is_folder_end() const;

            bool operator==(const ConstIterator& other) const;
            bool operator!=(const ConstIterator& other) const;

        };

        Iterator begin();
        Iterator end();
        ConstIterator begin() const;
        ConstIterator end() const;
        ConstIterator cbegin();
        ConstIterator cend();

        /// @brief Used to add a playlist to a folder specified by `folder_hierarchy` (or to the root if `folder_hierarchy` is empty)
        /// @param id 
        /// @param folder_hierarchy List of folders in hierarchical order (top to bottom)
        /// @return `true` if the folder hierarchy exists, `false` otherwise
        bool add_playlist(const std::string& id, const std::vector<std::string>& folder_hierarchy);

        /// @brief Used to add an empty folder to a parent folder specified by `folder_hierarchy` (or to the root if `folder_hierarchy` is empty)
        /// @param name 
        /// @param folder_hierarchy List of folders in hierarchical order (top to bottom)
        /// @return `true` if the folder hierarchy exists, `false` otherwise
        bool add_folder(const std::string& name, const std::vector<std::string>& folder_hierarchy);

        /// @brief Used to remove a playlist from a folder specified by `folder_hierarchy` (or from the root if `folder_hierarchy` is empty)
        /// @param id 
        /// @param folder_hierarchy List of folders in hierarchical order (top to bottom)
        /// @return `true` if the playlist was found, `false` otherwise
        bool remove_playlist(const std::string& id, const std::vector<std::string>& folder_hierarchy);

        /// @brief Used to recursively remove a folder from a parent folder specified by `folder_hierarchy` (or from the root if `folder_hierarchy` is empty)
        /// @param name 
        /// @param folder_hierarchy
        /// @return The playlists included in the removed folder, empty list if the folder wasn't found
        std::vector<std::string> remove_folder(const std::string& name, const std::vector<std::string>& folder_hierarchy);

        /// @brief Clears the tree
        void clear();

        /// @return A string representation of this tree object
        std::string string() const;

    };

}

#endif